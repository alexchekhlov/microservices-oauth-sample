package ac.cals.config;

import ac.common.auth.BaseUser;
import ac.common.auth.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${acoauth.clientId}")
	private String clientId;

	@Value("${acoauth.clientSecret}")
	private String clientSecret;

	@Value("${acoauth.checkTokenUrl}")
	private String checkTokenUrl;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin();
		http
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers("/authuser").permitAll()
				.antMatchers("/authuser/**").permitAll()
				.antMatchers("/h2/**").permitAll()
				.anyRequest().authenticated().and().rememberMe().alwaysRemember(true);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new CustomTokenEnhancer();
		return converter;
	}

	@Bean
	public RemoteTokenServices remoteTokenServices() {
		RemoteTokenServices services = new RemoteTokenServices();
		services.setCheckTokenEndpointUrl(checkTokenUrl);
		services.setClientId(clientId);
		services.setClientSecret(clientSecret);
		services.setAccessTokenConverter(accessTokenConverter());
		return services;
	}

	protected static class CustomTokenEnhancer extends JwtAccessTokenConverter {

		@Override
		public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
			OAuth2Authentication authentication = super.extractAuthentication(map);
			Long userId = Long.valueOf((String) map.get("userId"));
			String authority = (String) map.get("authority");
			UserRole role = UserRole.valueOf(authority);
			BaseUser user = new BaseUser(userId, (String) authentication.getPrincipal(), role);

			authentication = new OAuth2Authentication(authentication.getOAuth2Request(),
					new UsernamePasswordAuthenticationToken(user, "N/A", authentication.getUserAuthentication().getAuthorities()));
			return authentication;
		}
	}
}
