package com.appcity.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-preguntasrespuestas")
public interface PreguntasRespuestasFeignClient {

	@PostMapping("/preguntasrespuestas/crear")
	public void crearProyecto(@RequestParam("nombre") String nombre);

	@DeleteMapping("/preguntasrespuestas/borrarPreguntas/{nombre}")
	public void borrarPreguntas(@PathVariable String nombre);

}
