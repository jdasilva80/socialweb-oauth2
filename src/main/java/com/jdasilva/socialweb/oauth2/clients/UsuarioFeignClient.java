package com.jdasilva.socialweb.oauth2.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.jdasilva.socialweb.commons.models.entity.Usuario;

@FeignClient("socialweb-usuarios")
public interface UsuarioFeignClient {

	@GetMapping("/usuarios/search/buscar-nombre")
	public Usuario findByUserName(@RequestParam String username);

	@PutMapping("/usuarios/{id}")
	public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id);
}
