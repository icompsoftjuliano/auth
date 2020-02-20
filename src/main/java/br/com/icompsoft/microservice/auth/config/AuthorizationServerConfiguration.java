package br.com.icompsoft.microservice.auth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import br.com.icompsoft.microservice.auth.domain.Authorities;

@Configuration
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	private static PasswordEncoder encoder;

	private String clientId = "coderef";

	private String[] authorizedGrantTypes = { "password", "refresh_token" };

	private String resourceIds = "resources";

	private String[] scopes = { "read", "write" };

	private String secret = "$2a$10$p9Pk0fQNAQSesI4vuvKA0OZanDD2";

	private Integer accessTokenValiditySeconds = 1800;

	@Autowired
	DataSource dataSource;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Bean
	public JdbcTokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager).tokenStore(tokenStore());
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		.withClient("loja")
		.secret(encoder.encode("lojapwd"))
		.authorizedGrantTypes("password")
		.scopes("web", "mobile");
//		clients.jdbc(dataSource).withClient(clientId).authorizedGrantTypes(authorizedGrantTypes)
//				.authorities(Authorities.names()).resourceIds(resourceIds).scopes(scopes).secret(secret)
//				.accessTokenValiditySeconds(accessTokenValiditySeconds);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		if (encoder == null) {
			encoder = new BCryptPasswordEncoder();
		}
		return encoder;
	}
}