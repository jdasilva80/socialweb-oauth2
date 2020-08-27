package com.jdasilva.socialweb.oauth2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/oauth2")
public class ControllerPrueba {
	
	@GetMapping(value = { "/prueba" })
	public @ResponseBody Integer cargarProductosXml() {

		return 1;
	}

}
