package com.jdasilva.socialweb.oauth2.security.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.jdasilva.socialweb.commons.models.usuarios.entity.Usuario;
import com.jdasilva.socialweb.oauth2.services.IUsuarioService;

//import brave.Tracer;
import feign.FeignException;

@Component
public class AuthenticactionSuccessErrorHandler implements AuthenticationEventPublisher {

	@Autowired
	IUsuarioService usuarioService;

//	@Autowired
//	Tracer tracer;

	Logger log = LoggerFactory.getLogger(AuthenticactionSuccessErrorHandler.class);

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		log.info(" usuario logueado username" + userDetails.getUsername());

		Usuario usuario = usuarioService.findByUserName(userDetails.getUsername());
		if (usuario.getIntentos() != null && usuario.getIntentos() > 0) {
			usuario.setIntentos(0);
			usuarioService.update(usuario, usuario.getId());
		}

	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {

		StringBuilder sb = new StringBuilder();
		sb.append(" error en el login !!! " + exception.getMessage());
		log.error(sb.toString());

		try {
			Usuario usuario = usuarioService.findByUserName(authentication.getName());

			if (usuario.getIntentos() == null) {
				usuario.setIntentos(0);
			}

			usuario.setIntentos(usuario.getIntentos() + 1);

			log.info("Intentos actuales es de " + usuario.getIntentos());

			if (usuario.getIntentos() >= 3) {

				usuario.setActivo(false);

				log.info(String.format("El usuario %s está deshabilitado por número de intentos fallidos.",
						authentication.getName()));
			}
			sb.append(" -número de intentos " + usuario.getIntentos());

			usuarioService.update(usuario, usuario.getId());

			//tracer.currentSpan().tag("error.mensaje", sb.toString());

		} catch (FeignException e) {

			String error = String.format("El usuario %s no existe", authentication.getName());
			sb.append(" - " + error);
			//tracer.currentSpan().tag("error.mensaje", sb.toString());
			log.error(error);
		}

	}

}
