package com.appcity.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-busqueda")
public interface BusquedaFeignClient {

	@PutMapping("/busqueda/editarProyecto")
	public void editarProyecto(@RequestParam String nombre);

	@PutMapping("/busqueda/eliminarProyecto")
	public void eliminarProyecto(@RequestParam String nombre);

}
