package ac.system.auth.service;

import ac.system.auth.domain.AuthUser;
import ac.system.auth.domain.UserEntity;
import ac.system.auth.repository.UserRepository;
import ac.common.auth.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthUserDetailService userDetailService;

	@Test
	void loadUserByUsername() {
		String username = "user";
		when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			userDetailService.loadUserByUsername(username);
		});

		UserEntity entity = mock(UserEntity.class);
		when(entity.getId()).thenReturn(1L);
		when(entity.getUsername()).thenReturn(username);
		when(entity.getPassword()).thenReturn("pass");
		when(entity.getRole()).thenReturn(UserRole.ADMIN);
		when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(entity));

		AuthUser result = (AuthUser) userDetailService.loadUserByUsername(username);
		Assertions.assertEquals(1L, result.getId());
		Assertions.assertEquals(username, result.getUsername());
		Assertions.assertEquals("pass", result.getPassword());
		Assertions.assertEquals(1, result.getAuthorities().size());
		Assertions.assertEquals(UserRole.ADMIN.name(), ((GrantedAuthority) (result.getAuthorities().toArray()[0])).getAuthority());
	}
}