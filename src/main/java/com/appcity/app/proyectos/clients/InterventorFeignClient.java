package com.appcity.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-interventor")
public interface InterventorFeignClient {

	@PutMapping("/interventor/peticionEliminarProyecto")
	public void peticionEliminarProyecto(@RequestParam String username);
	
}
