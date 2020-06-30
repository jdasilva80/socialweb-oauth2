package com.jdasilva.socialweb.oauth2.services;

import com.jdasilva.socialweb.commons.models.entity.Usuario;

public interface IUsuarioService {

	public Usuario findByUserName(String username);

	public Usuario update(Usuario usuario, Long id);
}
