package ac.system.auth.controller;

import ac.system.auth.dto.UserDTO;
import ac.system.auth.service.UserService;
import ac.common.auth.BaseUser;
import ac.common.auth.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class SignupControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private SignupController controller;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void saveUser() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		params.put("username", List.of("test"));
		params.put("password", List.of("pass"));

		ArgumentCaptor<UserDTO> userCapture = ArgumentCaptor.forClass(UserDTO.class);
		ArgumentCaptor<BaseUser> adminCapture = ArgumentCaptor.forClass(BaseUser.class);

		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(params))
				.andExpect(status().is(HttpStatus.CREATED.value()));

		verify(userService, times(1)).saveUser(userCapture.capture(), adminCapture.capture());
		Assertions.assertEquals(userCapture.getValue().getUsername(), "test");
		Assertions.assertEquals(userCapture.getValue().getPassword(), "pass");
		Assertions.assertEquals(userCapture.getValue().getRole(), UserRole.USER);

		Assertions.assertEquals(adminCapture.getValue().getRole(), UserRole.ADMIN);
	}
}