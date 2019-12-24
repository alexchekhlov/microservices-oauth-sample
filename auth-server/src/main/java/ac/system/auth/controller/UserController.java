package ac.system.auth.controller;

import ac.system.auth.dto.UserDTO;
import ac.system.auth.service.UserService;
import ac.common.auth.BaseUser;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Api(value = "User management", description = "REST API for User management", tags = {"User"})
public class UserController {

	@Autowired
	private UserService userService;

	@PutMapping
	@PreAuthorize("(hasAnyAuthority('ADMIN', 'MANAGER') || (#user.id == authentication.principal.id)) && authentication.principal.role.priority <= #user.role.priority")
	@ApiOperation(value = "Update user", response = UserDTO.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "User saved"),
			@ApiResponse(code = 401, message = "Unauthorized")
	})
	public UserDTO saveUser(@RequestBody UserDTO user, OAuth2Authentication authentication) {
		return userService.saveUser(user, (BaseUser) authentication.getPrincipal());
	}

	@DeleteMapping("/{userId}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Deletes user")
	@ApiResponses({
			@ApiResponse(code = 204, message = "User deleted"),
			@ApiResponse(code = 401, message = "Unauthorized")
	})

	public void deleteUser(@ApiParam("ID of user to delete") @PathVariable("userId") Long userId, OAuth2Authentication authentication) {
		userService.deleteUser(userId, (BaseUser) authentication.getPrincipal());
	}

	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') && authentication.principal.role.priority <= #user.role.priority")
	@ApiOperation(value = "Create user", response = UserDTO.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "User saved"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 409, message = "Username exists")
	})
	public UserDTO createUser(@RequestBody UserDTO user, OAuth2Authentication authentication) {
		user.setId(null);
		return userService.saveUser(user, (BaseUser) authentication.getPrincipal());
	}

	@PostMapping("/list")
	@Secured({"ADMIN", "MANAGER"})
	@ApiOperation(value = "Get list of users. Supports pagination")
	public Page<UserDTO> getUsers(@RequestBody QueryFilteredPageRequest qpageRequest) {
		return userService.getUsers(qpageRequest);
	}

	@GetMapping("/{userId}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') || (#userId == authentication.principal.id)")
	@ApiOperation(value = "Get User details")
	public UserDTO getUserDetails(@ApiParam("User's ID") @PathVariable(name = "userId") Long userId) {
		return userService.getUser(userId);
	}
}
