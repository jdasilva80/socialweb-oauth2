package com.jdasilva.socialweb.oauth2.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@RefreshScope//se combina con actuator para refrescar las configuraciones (config-server)
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private InfoAditionalToken infoAditionalToken;

	@Value("${config.security.oauth.jwt.key}")
	private String jwtKey;

	@Value("${config.security.oauth.client.id}")
	private String clientId;

	@Value("${config.security.oauth.client.secret}")
	private String clientSecret;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		// es el endpoint para generar el token, con la ruta POST: /oauth/token (permitall para que la ruta sea pública)
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");// ruta para validar el token
		// estos 2 endpoints están protegidos por Header authorization http Basic (client id,
		// client secret), en cambio el token se envia como Bearer.
	}

	@Override 
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		
		//aquí se registran los clientes front (por ejemplo angular, android, react,...)
		clients.inMemory().withClient(clientId)
		.secret(passwordEncoder.encode(clientSecret))
		.scopes("read", "write", "app")
		.authorizedGrantTypes("password", "refresh_token")
		.accessTokenValiditySeconds(3600)
		.refreshTokenValiditySeconds(3600);
		
	}

	@Override//Es el endpoint (/oauth/token) de OAUTH2 para la autenticación, en este método configuramos el token de tipo JWT
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

		// información adicional en el token.
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAditionalToken, accessTokenConverterJwt()));

		// El access token converter es la información por defecto(username, role, fecha de expiración)..
		endpoints.authenticationManager(authManager).tokenStore(tokenStoreJwt())
				.accessTokenConverter(accessTokenConverterJwt()).tokenEnhancer(tokenEnhancerChain);
	}

	@Bean
	public JwtTokenStore tokenStoreJwt() {

		return new JwtTokenStore(accessTokenConverterJwt());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverterJwt() {

		JwtAccessTokenConverter tokenConverterJwt = new JwtAccessTokenConverter();
		tokenConverterJwt.setSigningKey(jwtKey);//firma del token

		return tokenConverterJwt;
	}

}
