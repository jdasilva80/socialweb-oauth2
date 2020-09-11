package com.jdasilva.socialweb.oauth2.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.jdasilva.socialweb.commons.models.usuarios.entity.Usuario;
import com.jdasilva.socialweb.oauth2.services.IUsuarioService;

@Component
public class InfoAditionalToken implements TokenEnhancer {

	@Autowired
	private IUsuarioService usuairoService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

		Map<String, Object> infoMap = new HashMap<>();

		Usuario usuario = usuairoService.findByUserName(authentication.getName());

		infoMap.put("nombre", usuario.getNombre());
		infoMap.put("apellidos", usuario.getApellidos());
		infoMap.put("email", usuario.getEmail());
		infoMap.put("id", usuario.getId());
		infoMap.put("contactos", usuario.getContactos());

		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(infoMap);

		return accessToken;
	}

}
