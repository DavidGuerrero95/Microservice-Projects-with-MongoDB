package com.appcity.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-notificaciones")
public interface NotificacionesFeignClient {

	@PostMapping("/notificaciones/editEnabled")
	public void enviarMensajeEnabled(@RequestParam String nombre, @RequestParam Boolean enabled);

	@PostMapping("/notificaciones/editEstado")
	public void enviarMensajeEstado(@RequestParam String nombre, @RequestParam Integer estado);

}
