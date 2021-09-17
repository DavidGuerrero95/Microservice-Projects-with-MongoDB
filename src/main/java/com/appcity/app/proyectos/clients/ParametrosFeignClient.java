package com.appcity.app.proyectos.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "app-parametrizacion")
public interface ParametrosFeignClient {

	@GetMapping("/parametros/get/labelProyectos")
	public List<Integer> getLabelProyectos();

	@PutMapping("/parametros/editar/labelProyectosManejo")
	public Boolean editarLabelProyectosManejo();

	@PutMapping("/parametros/editar/labelProyectosManejoDel/{number}")
	public void editarLabelProyectosManejoDelete(@PathVariable("number") Integer number);

}
