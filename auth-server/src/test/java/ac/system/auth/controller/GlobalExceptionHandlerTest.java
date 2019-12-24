package ac.system.auth.controller;

import ac.system.auth.exception.UserExistsException;
import ac.system.auth.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private SignupController controller;

	private MockMvc mockMvc;

	private MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

	@BeforeAll
	public void beforeAll() {
		params.put("username", List.of("test"));
		params.put("password", List.of("pass"));
	}

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
	}

	@Test
	void handleUserExistsException() throws Exception {
		when(userService.saveUser(any(), any())).thenThrow(new UserExistsException("test"));
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(params))
				.andExpect(status().is(HttpStatus.CONFLICT.value()));
	}

	@Test
	void handleUnathorizedException() throws Exception {
		when(userService.saveUser(any(), any())).thenThrow(new UnauthorizedUserException("test"));
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(params))
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
	}

	@Test
	void handleNoSuchElementException() throws Exception {
		when(userService.saveUser(any(), any())).thenThrow(new NoSuchElementException());
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(params))
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void testHandleGlobalEception() throws Exception {
		when(userService.saveUser(any(), any())).thenThrow(new RuntimeException());
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(params))
				.andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
}