package ac.system.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ac.system.auth.dto.UserDTO;
import ac.system.auth.service.UserService;
import ac.common.auth.BaseUser;
import ac.common.auth.UserRole;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: testing oauth2 with mockmvc requires more effort. No time now :(
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController controller;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void saveUser() {
		UserDTO user = mock(UserDTO.class);
		UserDTO expected = mock(UserDTO.class);
		BaseUser baseUser = mock(BaseUser.class);
		OAuth2Authentication auth = mock(OAuth2Authentication.class);

		when(auth.getPrincipal()).thenReturn(baseUser);
		when(userService.saveUser(user, baseUser)).thenReturn(expected);
		UserDTO actual = controller.saveUser(user, auth);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void deleteUser() {
		Long userId = 1L;
		BaseUser baseUser = mock(BaseUser.class);
		OAuth2Authentication auth = mock(OAuth2Authentication.class);
		when(auth.getPrincipal()).thenReturn(baseUser);

		controller.deleteUser(userId, auth);
		verify(userService, times(1)).deleteUser(userId, baseUser);
	}

	@Test
	void createUser() {
		UserDTO user = mock(UserDTO.class);
		UserDTO expected = mock(UserDTO.class);
		BaseUser baseUser = mock(BaseUser.class);
		OAuth2Authentication auth = mock(OAuth2Authentication.class);

		when(auth.getPrincipal()).thenReturn(baseUser);

		when(userService.saveUser(user, baseUser)).thenReturn(expected);

		UserDTO actual = controller.createUser(user, auth);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	void getUsers() throws Exception {
		QueryFilteredPageRequest request = new QueryFilteredPageRequest();
		request.setFilter("test");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		String requestJson = mapper.writeValueAsString(request);

		UserDTO user = new UserDTO("test", null, null);
		PageImpl<UserDTO> expected = new PageImpl<>(List.of(user));
		when(userService.getUsers(any())).thenReturn(expected);

		MvcResult result = mockMvc.perform(post("/user/list")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(requestJson))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andReturn();

		String response = result.getResponse().getContentAsString();

		//it's tricky to parse response since Page doesn't have default constructor
		Assertions.assertTrue(response.contains("{\"id\":null,\"username\":\"test\",\"role\":null}"));
		Assertions.assertTrue(response.contains("\"totalElements\":1"));
	}

	@Test
	void getUserDetails() throws Exception {
		Long userId = 1L;
		UserDTO user = new UserDTO("test", "pass", UserRole.USER);
		user.setId(userId);
		when(userService.getUser(userId)).thenReturn(user);

		MvcResult result = mockMvc.perform(get("/user/" + userId))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andReturn();

		String response = result.getResponse().getContentAsString();
		UserDTO actual = new ObjectMapper().readValue(response, UserDTO.class);
		Assertions.assertEquals(user.getId(), actual.getId());
		Assertions.assertEquals(user.getUsername(), actual.getUsername());
		Assertions.assertEquals(user.getRole(), actual.getRole());
		Assertions.assertNull(actual.getPassword());
	}
}