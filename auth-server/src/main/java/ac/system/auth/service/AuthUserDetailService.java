package ac.system.auth.service;

import ac.system.auth.repository.UserRepository;
import ac.system.auth.domain.AuthUser;
import ac.system.auth.domain.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	public AuthUserDetailService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found:" + username));
		GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
		return new AuthUser(user.getId(), user.getUsername(), user.getPassword(), List.of(authority));
	}
}
