package ac.system.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResorseServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin();
		http
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers("/h2/**").permitAll()
				.antMatchers("/webjars/springfox-swagger-ui/**").permitAll()
				.antMatchers("/swagger-ui.html**").permitAll()
				.antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/v2/api-docs**").permitAll()
				.antMatchers("/oauth/token").permitAll()
				.antMatchers("/signup**").permitAll()
				.antMatchers("/user/**").authenticated()
				.antMatchers("/**").authenticated()
				.anyRequest().authenticated();
	}
}
