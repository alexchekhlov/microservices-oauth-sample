package ac.system.auth.service;

import ac.system.auth.dto.UserDTO;
import ac.system.auth.domain.UserEntity;
import ac.system.auth.exception.UserExistsException;
import ac.system.auth.repository.UserRepository;
import ac.common.auth.BaseUser;
import ac.common.auth.UserRole;
import ac.common.jpa.converter.PageRequestToPageableConverter;
import ac.common.jpa.criteria.QueryParser;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private WebClient webClient;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private PageRequestToPageableConverter pageRequestToPageableConverter;

	@Mock
	private QueryParser queryFilterParser;

	@InjectMocks
	private UserServiceImpl userService;

	private String url = "http://calories-service";

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(userService, "notifyUserChangedUrl", url);
	}

	@Test
	void testSaveNew() {
		UserDTO userDTO = new UserDTO("testUser", "pass", UserRole.ADMIN);
		UserEntity userEntity = new UserEntity("testUser", "pass", UserRole.ADMIN);
		when(modelMapper.map(userDTO, UserEntity.class)).thenReturn(userEntity);
		when(passwordEncoder.encode(userEntity.getPassword())).thenReturn("encodedpass");

		UserEntity savedEntity = new UserEntity(userDTO.getUsername(), "encodedpass", UserRole.ADMIN);
		savedEntity.setId(1L);
		when(userRepository.findByUsernameIgnoreCase(userDTO.getUsername())).thenReturn(Optional.empty());
		when(userRepository.save(userEntity)).thenReturn(savedEntity);

		WebClient.RequestBodyUriSpec request = mock(WebClient.RequestBodyUriSpec.class);
		when(webClient.post()).thenReturn(request);
		WebClient.RequestHeadersSpec bodyRequest = mock(WebClient.RequestHeadersSpec.class);
		Mono clientMono = mock(Mono.class);
		when(request.uri(url)).thenReturn(request);
		when(request.body(any())).thenReturn(bodyRequest);
		when(bodyRequest.exchange()).thenReturn(clientMono);

		UserDTO expected = mock(UserDTO.class);

		when(modelMapper.map(savedEntity, UserDTO.class)).thenReturn(expected);

		BaseUser admin = new BaseUser(11L, "admin", UserRole.ADMIN);
		UserDTO actual = userService.saveUser(userDTO, admin);
		Assertions.assertEquals(expected, actual);
		verify(userRepository, times(1)).save(userEntity);
		verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
	}

	@Test
	void testSaveExistingUsernameChanged() {
		UserDTO userDTO = new UserDTO("testUser", null, UserRole.ADMIN);
		userDTO.setId(1L);

		UserEntity mappedEntity = new UserEntity("oldtestUser", null, UserRole.ADMIN);
		mappedEntity.setId(1L);

		UserEntity userEntity = new UserEntity("oldtestUser", "pass", UserRole.ADMIN);
		userEntity.setId(1L);

		when(userRepository.findByUsernameIgnoreCase(userDTO.getUsername())).thenReturn(Optional.empty());

		when(modelMapper.map(userDTO, UserEntity.class)).thenReturn(mappedEntity);
		when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
		when(userRepository.save(mappedEntity)).thenReturn(mappedEntity);

		WebClient.RequestBodyUriSpec request = mock(WebClient.RequestBodyUriSpec.class);
		when(webClient.post()).thenReturn(request);
		WebClient.RequestHeadersSpec bodyRequest = mock(WebClient.RequestHeadersSpec.class);

		Mono clientMono = mock(Mono.class);
		when(request.uri(url)).thenReturn(request);
		when(request.body(any())).thenReturn(bodyRequest);
		when(bodyRequest.exchange()).thenReturn(clientMono);

		UserDTO expected = mock(UserDTO.class);

		when(modelMapper.map(mappedEntity, UserDTO.class)).thenReturn(expected);

		BaseUser admin = new BaseUser(11L, "admin", UserRole.ADMIN);
		UserDTO actual = userService.saveUser(userDTO, admin);
		Assertions.assertEquals(expected, actual);
		verify(userRepository, times(1)).save(mappedEntity);
		verify(passwordEncoder, never()).encode(anyString());
	}

	@Test
	void testSaveExistingUsernameChangedAndPasswordChanged() {
		UserDTO userDTO = new UserDTO("testUser", "newPassword", UserRole.ADMIN);
		userDTO.setId(1L);

		UserEntity mappedEntity = new UserEntity("oldtestUser", "newPassword", UserRole.ADMIN);
		mappedEntity.setId(1L);

		UserEntity userEntity = new UserEntity("oldtestUser", "newPassword", UserRole.ADMIN);
		userEntity.setId(1L);

		when(userRepository.findByUsernameIgnoreCase(userDTO.getUsername())).thenReturn(Optional.empty());

		when(modelMapper.map(userDTO, UserEntity.class)).thenReturn(mappedEntity);
		when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
		when(userRepository.save(mappedEntity)).thenReturn(mappedEntity);

		WebClient.RequestBodyUriSpec request = mock(WebClient.RequestBodyUriSpec.class);
		when(webClient.post()).thenReturn(request);
		WebClient.RequestHeadersSpec bodyRequest = mock(WebClient.RequestHeadersSpec.class);

		Mono clientMono = mock(Mono.class);
		when(request.uri(url)).thenReturn(request);
		when(request.body(any())).thenReturn(bodyRequest);
		when(bodyRequest.exchange()).thenReturn(clientMono);

		UserDTO expected = mock(UserDTO.class);

		when(modelMapper.map(mappedEntity, UserDTO.class)).thenReturn(expected);

		BaseUser admin = new BaseUser(11L, "admin", UserRole.ADMIN);
		UserDTO actual = userService.saveUser(userDTO, admin);

		Assertions.assertEquals(expected, actual);
		verify(userRepository, times(1)).save(mappedEntity);
		verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
	}

	@Test
	void testSaveExistingUsernameNotChanged() {
		UserDTO userDTO = new UserDTO("testUser", null, UserRole.ADMIN);
		userDTO.setId(1L);

		UserEntity mappedEntity = new UserEntity("testUser", null, UserRole.ADMIN);
		mappedEntity.setId(1L);

		UserEntity userEntity = new UserEntity("testUser", "pass", UserRole.ADMIN);
		userEntity.setId(1L);

		when(userRepository.findByUsernameIgnoreCase(userDTO.getUsername())).thenReturn(Optional.of(userEntity));

		when(modelMapper.map(userDTO, UserEntity.class)).thenReturn(mappedEntity);

		when(userRepository.save(mappedEntity)).thenReturn(mappedEntity);

		UserDTO expected = mock(UserDTO.class);

		when(modelMapper.map(mappedEntity, UserDTO.class)).thenReturn(expected);

		BaseUser admin = new BaseUser(11L, "admin", UserRole.ADMIN);
		UserDTO actual = userService.saveUser(userDTO, admin);
		Assertions.assertEquals(expected, actual);
		verify(userRepository, times(1)).save(mappedEntity);
		verify(passwordEncoder, never()).encode(userDTO.getPassword());
		verify(webClient, never()).post();
	}

	@Test
	void testSaveUserNotFound() {
		UserDTO userDTO = new UserDTO("testUser", "pass", UserRole.ADMIN);
		userDTO.setId(1L);
		UserEntity mappedEntity = new UserEntity("testUser", null, UserRole.ADMIN);
		mappedEntity.setId(1L);

		when(userRepository.findByUsernameIgnoreCase(userDTO.getUsername())).thenReturn(Optional.empty());
		when(modelMapper.map(userDTO, UserEntity.class)).thenReturn(mappedEntity);
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		BaseUser admin = new BaseUser(11L, "admin", UserRole.ADMIN);
		Assertions.assertThrows(RuntimeException.class, () -> {
			userService.saveUser(userDTO, admin);
		});
	}

	@Test
	void testSaveUserExistsNotFound() {
		UserDTO userDTO = new UserDTO("testUser", "pass", UserRole.ADMIN);
		userDTO.setId(1L);
		UserEntity existing = new UserEntity("testUser", null, UserRole.ADMIN);
		existing.setId(2L);

		when(userRepository.findByUsernameIgnoreCase(userDTO.getUsername())).thenReturn(Optional.of(existing));
		BaseUser admin = new BaseUser(11L, "admin", UserRole.ADMIN);
		Assertions.assertThrows(UserExistsException.class, () -> {
			userService.saveUser(userDTO, admin);
		});
	}

	@Test
	void testSaveUserCantModify() {
		UserDTO userDTO = new UserDTO("testUser", "pass", UserRole.ADMIN);
		userDTO.setId(1L);
		UserEntity mappedEntity = new UserEntity("testUser", null, UserRole.ADMIN);
		mappedEntity.setId(1L);
		when(modelMapper.map(userDTO, UserEntity.class)).thenReturn(mappedEntity);
		when(userRepository.findById(1L)).thenReturn(Optional.of(mappedEntity));
		BaseUser user = new BaseUser(11L, "admin", UserRole.USER);
		Assertions.assertThrows(UnauthorizedUserException.class, () -> {
			userService.saveUser(userDTO, user);
		});
	}

	@Test
	void deleteUser() {
		Long id = 1L;
		UserEntity userEntity = new UserEntity("testUser", "aaa", UserRole.MANAGER);

		when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

		WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		when(webClient.delete()).thenReturn(headersUriSpec);
		WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
		when(headersUriSpec.uri(url + "/" + id)).thenReturn(headersSpec);
		when(headersSpec.exchange()).thenReturn(mock(Mono.class));

		BaseUser user = new BaseUser(11L, "admin", UserRole.ADMIN);
		userService.deleteUser(id, user);

		verify(userRepository).delete(userEntity);

		user.setRole(UserRole.USER);
		Assertions.assertThrows(UnauthorizedUserException.class, () -> {
			userService.deleteUser(id, user);
		});
	}

	@Test
	void getUsers() {
		//Test with null request
		Specification spec = mock(Specification.class);
		PageRequest pageRequest = mock(PageRequest.class);

		when(pageRequestToPageableConverter.convert(null, Sort.by("username"))).thenReturn(pageRequest);
		when(queryFilterParser.parse(eq(null), any(Function.class))).thenReturn(spec);

		Page<UserEntity> entitiesPage = mock(Page.class);
		Page<UserDTO> resultPage = mock(Page.class);

		when(userRepository.findAll(spec, pageRequest)).thenReturn(entitiesPage);

		when(entitiesPage.map(any(Function.class))).thenReturn(resultPage);

		Page<UserDTO> actual = userService.getUsers(null);
		Assertions.assertEquals(resultPage, actual);

		//Test non-null request
		QueryFilteredPageRequest request = mock(QueryFilteredPageRequest.class);
		when(request.getFilter()).thenReturn("filter");

		when(pageRequestToPageableConverter.convert(request, Sort.by("username"))).thenReturn(pageRequest);
		when(queryFilterParser.parse(eq("filter"), any(Function.class))).thenReturn(spec);

		actual = userService.getUsers(request);
		Assertions.assertEquals(resultPage, actual);
	}

	@Test
	void getUser() {
		Long id = 1L;

		when(userRepository.findById(id)).thenReturn(Optional.empty());
		Assertions.assertThrows(RuntimeException.class, () -> {
			userService.getUser(id);
		});

		UserEntity entity = mock(UserEntity.class);
		when(userRepository.findById(id)).thenReturn(Optional.of(entity));
		UserDTO expected = mock(UserDTO.class);
		when(modelMapper.map(entity, UserDTO.class)).thenReturn(expected);

		UserDTO actual = userService.getUser(id);

		Assertions.assertEquals(expected, actual);
	}

}