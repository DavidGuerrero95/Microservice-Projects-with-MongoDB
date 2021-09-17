package com.appcity.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.appcity.app.proyectos.models.Muro;
import com.appcity.app.proyectos.models.Proyectos;

@FeignClient(name = "app-muro")
public interface MuroFeignClient {

	@PostMapping("/muros/crearProyectos")
	public Integer crearMurosProyectos(@RequestBody Proyectos proyectos);

	@GetMapping("/muros/buscar/{codigo}")
	public Muro getMuroCodigo(@PathVariable("codigo") Integer codigo);

	@PutMapping("/muros/eliminarProyecto/{codigo}")
	public void eliminarProyecto(@PathVariable Integer codigo, @RequestParam String nombre);

}
