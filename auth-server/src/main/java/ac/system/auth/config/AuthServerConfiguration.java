package ac.system.auth.config;

import ac.system.auth.domain.AuthUser;
import ac.common.auth.BaseUser;
import ac.common.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.LinkedHashMap;
import java.util.Map;

//TODO: add password encoder
@Configuration
@EnableAuthorizationServer
public class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

	@Value("${ac.oauth.clientId}")
	private String clientId;

	@Value("${ac.oauth.clientSecret}")
	private String clientSecret;

	@Value("${ac.oauth.accessTokenValidititySeconds}")
	private int accessTokenValiditySeconds;

	@Value("${ac.oauth.refreshTokenValiditySeconds}")
	private int refreshTokenValiditySeconds;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient(clientId)
				.secret(passwordEncoder.encode(clientSecret))
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds)
				.authorizedGrantTypes("password", "client_credentials", "refresh_token")
				.scopes("read", "write");
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.checkTokenAccess("permitAll()");
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints
				.tokenStore(tokenStore())
				.accessTokenConverter(accessTokenConverter())
				.userDetailsService(userDetailsService)
				.authenticationManager(authenticationManager);
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new CustomTokenEnhancer();
		return converter;
	}

//	@Bean
//	@Primary
//	public DefaultTokenServices tokenServices() {
//		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//		defaultTokenServices.setTokenStore(tokenStore());
//		defaultTokenServices.setSupportRefreshToken(true);
//		return defaultTokenServices;
//	}

	protected static class CustomTokenEnhancer extends JwtAccessTokenConverter {

		@Override
		public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
										 OAuth2Authentication authentication) {

			AuthUser user = (AuthUser) authentication.getPrincipal();
			Map<String, Object> info = new LinkedHashMap(
					accessToken.getAdditionalInformation());

			info.put("userId", user.getId().toString());
			info.put("authority", ((GrantedAuthority)(user.getAuthorities().toArray()[0])).getAuthority());
			DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
			customAccessToken.setAdditionalInformation(info);
			return super.enhance(customAccessToken, authentication);
		}

		@Override
		public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
			OAuth2Authentication authentication = super.extractAuthentication(map);
			Long userId = Long.valueOf((String) map.get("userId"));
			String authority = (String) map.get("authority");
			BaseUser user = new BaseUser(userId, (String) authentication.getPrincipal(), UserRole.valueOf(authority));

			authentication = new OAuth2Authentication(authentication.getOAuth2Request(),
					new UsernamePasswordAuthenticationToken(user, "N/A", authentication.getUserAuthentication().getAuthorities()));
			return authentication;
		}
	}
}
