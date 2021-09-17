package com.appcity.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-estadistica")
public interface EstadisticaFeignClient {

	@PostMapping("/estadistica/crearEna")
	public void crearEstadistica(@RequestParam("nombre") String nombre);

	@PutMapping("/estadistica/visualizaciones/{nombre}")
	public void aumentarVisualizaciones(@PathVariable("nombre") String nombre);

	@DeleteMapping("/estadistica/borrarEstadisticas/{nombre}")
	public void borrarEstadisticas(@PathVariable String nombre);

}
