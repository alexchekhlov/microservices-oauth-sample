package ac.system.auth.service;

import ac.system.auth.dto.UserDTO;
import ac.common.auth.BaseUser;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.springframework.data.domain.Page;

public interface UserService {
	UserDTO saveUser(UserDTO user, BaseUser modifier);
	void deleteUser(Long userId, BaseUser modifier);
	Page<UserDTO> getUsers(QueryFilteredPageRequest queryFilteredPageRequest);
	UserDTO getUser(Long id);
}
