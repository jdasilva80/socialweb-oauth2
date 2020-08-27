package com.jdasilva.socialweb.oauth2.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.jdasilva.socialweb.commons.models.entity.Usuario;
//import com.jdasilva.socialweb.oauth2.clients.UsuarioFeignClient;

//import brave.Tracer;
//import feign.FeignException;

@Service//proveedor de autenticación
public class UsuarioService implements UserDetailsService, IUsuarioService {

//	@Autowired
//	private UsuarioFeignClient usuarioFeignClient;
	
	@Autowired
	RestTemplate clienteRest;

//	@Autowired
//	private Tracer tracer;

	private Logger log = LoggerFactory.getLogger(UsuarioService.class);

	@Override //se hace el login, autenticacion ya sea por jpa, feign ....
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		try {
			//UserDetails representa un usuario de Spring Security
			Usuario usuario = findByUserName(username);			

			//transformamos los roles a roles tipo Spring Security(GrantedAuthority)
			List<GrantedAuthority> authorities = usuario.getRoles().stream()
					.map((role) -> new SimpleGrantedAuthority(role.getRole()))
					.peek((authority) -> log.info(authority.getAuthority())).collect(Collectors.toList());

			UserDetails userDetails = new User(username, usuario.getPassword(), usuario.getActivo(), true, true, true,
					authorities);

			log.info("se ha logueado el usuario  " + userDetails.getUsername());

			return userDetails;

//		} catch (FeignException e) {
		} catch (RestClientException e) { 

			String error = "Usuario ".concat(username).concat("no existe en el sistema, ").concat(e.getMessage());
			//tracer.currentSpan().tag("error.mensaje", error);
			
			//se recibirá un json "bad credentials"
			
			throw new UsernameNotFoundException(error);
		}
	}

//	@Override
//	public Usuario findByUserName(String username) {
//
//		return usuarioFeignClient.findByUserName(username);
//	}
//
//	@Override
//	public Usuario update(Usuario usuario, Long id) {
//
//		return usuarioFeignClient.update(usuario, id);
//	}
	
	@Override
	public Usuario findByUserName(String username) {

		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("username", username);

		Usuario usuario = clienteRest.getForObject(
				"https://soyjose-usuarios.herokuapp.com/usuarios/username/{username}", Usuario.class,
				pathVariables);

		return usuario;
	}

	@Override
	public Usuario update(Usuario usuario, Long id) {

		clienteRest.put("https://soyjose-usuarios.herokuapp.com/usuarios/".concat(id.toString()), usuario);

		return usuario;
	}

}
