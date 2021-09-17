package com.appcity.app.proyectos.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.appcity.app.proyectos.clients.BusquedaFeignClient;
import com.appcity.app.proyectos.clients.EstadisticaFeignClient;
import com.appcity.app.proyectos.clients.InterventorFeignClient;
import com.appcity.app.proyectos.clients.MuroFeignClient;
import com.appcity.app.proyectos.clients.NotificacionesFeignClient;
import com.appcity.app.proyectos.clients.ParametrosFeignClient;
import com.appcity.app.proyectos.clients.PreguntasRespuestasFeignClient;
import com.appcity.app.proyectos.clients.SubscripcionesFeignClient;
import com.appcity.app.proyectos.models.Proyectos;
import com.appcity.app.proyectos.repository.ProyectosRepository;

@RestController
public class ProyectosController {

	@Autowired
	ProyectosRepository proyectoRepository;

	@Autowired
	ParametrosFeignClient parametros;

	@Autowired
	EstadisticaFeignClient estadisticas;

	@Autowired
	MuroFeignClient muroService;

	@Autowired
	PreguntasRespuestasFeignClient preguntasRespuestas;

	@Autowired
	SubscripcionesFeignClient subscripciones;

	@Autowired
	BusquedaFeignClient busqueda;

	@Autowired
	NotificacionesFeignClient notificaciones;
	
	@Autowired
	InterventorFeignClient interventor;

	@GetMapping("/proyectos/listar")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> getProyectos() {
		return proyectoRepository.findAll();
	}

	@GetMapping("/proyectos")
	@ResponseStatus(code = HttpStatus.FOUND)
	public Proyectos findByNombreOrToken(@PathVariable String nombre, @PathVariable Integer token) {
		return proyectoRepository.findByNombreOrCodigoProyecto(nombre, token);
	}

	@PostMapping("/proyectos/crear")
	@ResponseStatus(code = HttpStatus.CREATED)
	public String crearProyectos(@RequestBody Proyectos proyectos) {
		List<Double> latLon = new ArrayList<Double>();
		if (proyectos.getMuro() == null) {

			if (proyectos.getNombre() != null || proyectos.getCreador() != null) {

				if (proyectos.getLocalizacion() != null) {
					BigDecimal bdlat = new BigDecimal(proyectos.getLocalizacion().get(0)).setScale(5,
							RoundingMode.HALF_UP);
					BigDecimal bdlon = new BigDecimal(proyectos.getLocalizacion().get(1)).setScale(5,
							RoundingMode.HALF_UP);
					latLon.add(bdlat.doubleValue());
					latLon.add(bdlon.doubleValue());
					proyectos.setLocalizacion(latLon);
				} else {
					proyectos.setLocalizacion(latLon);
				}
				try {
					List<Integer> listaLabel = parametros.getLabelProyectos();
					proyectos.setCodigoProyecto(listaLabel.get(listaLabel.size() - 1));
				} catch (Exception e) {
					List<Proyectos> listaLabel2 = proyectoRepository.findAll();
					proyectos.setCodigoProyecto(listaLabel2.size());
				}

				if (proyectos.getEnabled() == null) {
					proyectos.setEnabled(true);
				}

				try {
					proyectos.setMuro(muroService.crearMurosProyectos(proyectos));
				} catch (Exception e) {
					proyectos.setMuro(proyectoRepository.findAll().size());
				}

				try {
					estadisticas.crearEstadistica(proyectos.getNombre());

				} catch (Exception e) {
					System.out.println("Mensaje de eror: " + e.getMessage());
				}

				try {
					parametros.editarLabelProyectosManejo();
				} catch (Exception e) {
					System.out.println("Mensaje de eror: " + e.getMessage());
				}

				try {
					preguntasRespuestas.crearProyecto(proyectos.getNombre());
				} catch (Exception e) {
					System.out.println("Mensaje de eror: " + e.getMessage());
				}

				try {
					subscripciones.crearSubscripciones(proyectos.getNombre());
				} catch (Exception e) {
					System.out.println("Mensaje de eror: " + e.getMessage());
				}

				try {
					busqueda.editarProyecto(proyectos.getNombre());
				} catch (Exception e) {
					System.out.println("Mensaje de eror: " + e.getMessage());
				}

				try {
					Calendar c = Calendar.getInstance();

			        String dia = Integer.toString(c.get(Calendar.DATE));
			        String mes = Integer.toString(c.get(Calendar.MONTH));
			        String annio = Integer.toString(c.get(Calendar.YEAR));
			        String fecha = dia+"/"+mes+"/"+annio;
			        proyectos.setFecha(fecha);
					proyectoRepository.save(proyectos);

					return "Creacion exitosa, proyecto (Puesto en muro): " + proyectos.getNombre();
				} catch (Exception e) {
					return "Creacion fallida, proyecto: " + e.getMessage();
				}
			} else {
				return "Ingresar todos los datos del proyecto";
			}
		} else {
			if (proyectos.getLocalizacion() == null) {
				proyectos.setLocalizacion(muroService.getMuroCodigo(proyectos.getMuro()).getLocalizacion());
			} else {
				BigDecimal bdlat = new BigDecimal(proyectos.getLocalizacion().get(0)).setScale(5, RoundingMode.HALF_UP);
				BigDecimal bdlon = new BigDecimal(proyectos.getLocalizacion().get(1)).setScale(5, RoundingMode.HALF_UP);
				latLon.add(bdlat.doubleValue());
				latLon.add(bdlon.doubleValue());
				proyectos.setLocalizacion(latLon);
			}
			proyectos.setMuro(muroService.getMuroCodigo(proyectos.getMuro()).getCodigoMuro());
			proyectoRepository.save(proyectos);
			return "Creacion exitosa, proyecto: " + proyectos.getNombre();
		}

	}
	
	@PutMapping("/proyectos/eliminarAdmin/{name}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarAdmin(@PathVariable String name) {
		interventor.peticionEliminarProyecto(name);
	}

	@DeleteMapping("/proyectos/eliminar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public String eliminarProyectos(@PathVariable String nombre) {
		try {
			Proyectos proyectos = proyectoRepository.findByNombre(nombre);
			String id = proyectos.getId();
			proyectoRepository.deleteById(id);
			List<Proyectos> proyectosList = proyectoRepository.findAll();
			try {
				parametros.editarLabelProyectosManejoDelete(proyectosList.size());
			} catch (Exception e) {
				System.out.println("Error: Parametros " + e.getMessage());
			}
			try {
				busqueda.eliminarProyecto(nombre);
			} catch (Exception e) {
				System.out.println("Error: Busqueda " + e.getMessage());
			}
			try {
				muroService.eliminarProyecto(proyectos.getMuro(), nombre);
			} catch (Exception e) {
				System.out.println("Error: Muro " + e.getMessage());
			}
			try {
				estadisticas.borrarEstadisticas(nombre);
			} catch (Exception e) {
				System.out.println("Error: Estadisticas " + e.getMessage());
			}
			try {
				preguntasRespuestas.borrarPreguntas(nombre);
			} catch (Exception e) {
				System.out.println("Error: Preguntas y Respuestas " + e.getMessage());
			}
			try {
				subscripciones.borrarSuscripciones(nombre);
			} catch (Exception e) {
				System.out.println("Error: Suscripciones " + e.getMessage());
			}
			List<Integer> listaLabel = parametros.getLabelProyectos();
			for (int i = 0; i < proyectosList.size(); i++) {
				proyectosList.get(i).setCodigoProyecto(listaLabel.get(i));
				proyectoRepository.save(proyectosList.get(i));
			}
			return "Proyecto eliminado";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@GetMapping("/proyectos/descripcion/{nombre}")
	@ResponseStatus(code = HttpStatus.FOUND)
	public String descripcionMuro(@PathVariable("nombre") String nombre, @RequestBody Proyectos proyectos) {
		Proyectos question = proyectoRepository.findByNombre(nombre);
		return question.getDescripcion();
	}

	@GetMapping("/proyectos/listarByMuro/{codigo}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> getProyecyosByMuro(@PathVariable("codigo") Integer codigo) {
		List<Proyectos> lista = new ArrayList<Proyectos>();
		List<Proyectos> listaProyectos = proyectoRepository.findAll();
		for (int i = 0; i < listaProyectos.size(); i++) {
			if (listaProyectos.get(i).getMuro() == codigo) {
				lista.add(listaProyectos.get(i));
			}
		}
		return lista;
	}
	
	@GetMapping("/proyectos/verCreador/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> verCreador(@PathVariable String username){
		List<Proyectos> allProyectos = proyectoRepository.findAll();
		List<Proyectos> proyectos = new ArrayList<Proyectos>();
		for(int i=0; i<allProyectos.size(); i++) {
			if(allProyectos.get(i).getCreador().equals(username)) {
				proyectos.add(allProyectos.get(i));
			}
		}
		return proyectos;
	}

	@PutMapping("/proyectos/visualizaciones/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void visualizacion(@PathVariable("nombre") String nombre) {
		estadisticas.aumentarVisualizaciones(nombre);
	}

	@GetMapping("/proyectos/obtenerProyectoByNombre/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Proyectos getProyectosByNombre(@PathVariable("nombre") String nombre) {
		return proyectoRepository.findByNombre(nombre);
	}

	@PutMapping("/proyectos/editEnabled/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void editEnabled(@PathVariable("nombre") String nombre) {
		Proyectos proyecto = proyectoRepository.findByNombre(nombre);
		if (proyecto.getEnabled()) {
			proyecto.setEnabled(false);
		} else {
			proyecto.setEnabled(true);
		}
		proyectoRepository.save(proyecto);
		notificaciones.enviarMensajeEnabled(nombre, proyecto.getEnabled());
	}

	@PutMapping("/proyectos/editEstado/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void editEstado(@PathVariable("nombre") String nombre, @RequestBody Proyectos pro) {
		Proyectos proyecto = proyectoRepository.findByNombre(nombre);
		proyecto.setEstadoProyecto(pro.getEstadoProyecto());
		proyectoRepository.save(proyecto);
		notificaciones.enviarMensajeEstado(nombre, proyecto.getEstadoProyecto());
	}
	
	@PutMapping("/proyectos/editarProyectos/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void editarProyectos(@PathVariable String nombre, @RequestBody Proyectos proyectoEditar) {
		Proyectos proyecto = proyectoRepository.findByNombre(nombre);
		if(proyectoEditar.getNombre() != null) {proyecto.setNombre(proyectoEditar.getNombre());}
		if(proyectoEditar.getPalabrasClave() != null) {proyecto.setPalabrasClave(proyectoEditar.getPalabrasClave());}
		if(proyectoEditar.getLocalizacion() != null) {proyecto.setLocalizacion(proyectoEditar.getLocalizacion());}
		if(proyectoEditar.getResumen() != null) {proyecto.setResumen(proyectoEditar.getResumen());}
		if(proyectoEditar.getObjetivos() != null) {proyecto.setObjetivos(proyectoEditar.getObjetivos());}
		if(proyectoEditar.getDescripcion() != null) {proyecto.setDescripcion(proyectoEditar.getDescripcion());}
		if(proyectoEditar.getPresupuesto() != null) {proyecto.setPresupuesto(proyectoEditar.getPresupuesto());}
		if(proyectoEditar.getCronograma() != null) {proyecto.setCronograma(proyectoEditar.getCronograma());}
		if(proyectoEditar.getEnabled() != null) {proyecto.setEnabled(proyectoEditar.getEnabled());}
		if(proyectoEditar.getEstadoProyecto() != null) {proyecto.setEstadoProyecto(proyectoEditar.getEstadoProyecto());}
		if(proyectoEditar.getProyectoDesarrollo() != null) {proyecto.setProyectoDesarrollo(proyectoEditar.getProyectoDesarrollo());}
		if(proyectoEditar.getProyectoDesarrollo() != null) {proyecto.setProyectoDesarrollo(proyectoEditar.getProyectoDesarrollo());}
		proyectoRepository.save(proyecto);

	}
	
	@PutMapping("/proyectos/arreglarCreador")
	@ResponseStatus(code = HttpStatus.OK)
	public void arreglarCreador() {
		List<Proyectos> listaProyectos = proyectoRepository.findAll();
		for(int i=0; i<listaProyectos.size(); i++) {
			Calendar c = Calendar.getInstance();
			String dia = Integer.toString(c.get(Calendar.DATE));
	        String mes = Integer.toString(c.get(Calendar.MONTH));
	        String annio = Integer.toString(c.get(Calendar.YEAR));
	        String fecha = dia+"/"+mes+"/"+annio;
			listaProyectos.get(i).setFecha(fecha);
			proyectoRepository.save(listaProyectos.get(i));
		}
	}
	
	

}