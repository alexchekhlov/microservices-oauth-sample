package ac.system.auth.controller;

import ac.system.auth.dto.UserDTO;
import ac.system.auth.service.UserService;
import ac.common.auth.BaseUser;
import ac.common.auth.UserRole;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

@Controller
@RequestMapping("/signup")
@Api(value = "Sign Up", description = "REST API for Sign Up", tags = {"Sign Up11"})
public class SignupController {

	@Autowired
	private UserService userService;

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Self registering of new user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully registered"),
			@ApiResponse(code = 409, message = "Username exists")
	})
	void signUp(@ApiParam(value = "User credentials. Example: {username: your_name, password: your_pass}")
				@RequestParam HashMap<String, String> credentials) {

		String username = credentials.get("username");
		String password = credentials.get("password");
		UserDTO user = new UserDTO(username, password, UserRole.USER);
		BaseUser fakeAdmin = new BaseUser(null, null, UserRole.ADMIN);
		userService.saveUser(user, fakeAdmin);
	}
}
