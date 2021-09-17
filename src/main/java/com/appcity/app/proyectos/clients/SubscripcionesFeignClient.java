package com.appcity.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-subscripciones")
public interface SubscripcionesFeignClient {

	@PostMapping("/subscripciones/crear")
	public void crearSubscripciones(@RequestParam("nombre") String nombre);

	@DeleteMapping("/suscripciones/borrar/{nombre}")
	public void borrarSuscripciones(@PathVariable String nombre);

}
