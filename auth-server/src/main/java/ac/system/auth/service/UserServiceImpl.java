package ac.system.auth.service;

import ac.system.auth.dto.UserDTO;
import ac.system.auth.repository.UserRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import ac.system.auth.domain.UserEntity;
import ac.system.auth.exception.UserExistsException;
import ac.system.auth.jpa.converter.UserSpecification;
import ac.common.auth.BaseUser;
import ac.common.jpa.converter.PageRequestToPageableConverter;
import ac.common.jpa.criteria.QueryParser;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Value("${ac.calories-service.authuser.url}")
	private String notifyUserChangedUrl;

	@Autowired
	private QueryParser queryFilterParser;

	@Autowired
	private WebClient webClient;

	@Autowired
	private PageRequestToPageableConverter pageRequestToPageableConverter;

	@Override
	public UserDTO saveUser(UserDTO user, BaseUser modifier) {

		logger.info("Saving User: " + user.toString());

		UserEntity userByUserName = userRepository.findByUsernameIgnoreCase(user.getUsername()).orElse(null);

		if (userByUserName != null) {
			if (user.getId() != userByUserName.getId()) {
				throw new UserExistsException(user.getUsername());
			}
		}

		UserEntity entity = modelMapper.map(user, UserEntity.class);
		boolean notifyCaloriesService = true;
		if (entity.getId() != null) {
			UserEntity currentEntity = userByUserName != null ? userByUserName : userRepository.findById(entity.getId()).orElseThrow();

			//Managers can't modify admins
			if (currentEntity.getRole().getPriority() < modifier.getRole().getPriority()) {
				throw new UnauthorizedUserException("You can't modify users with higher role");
			}

			if (currentEntity.getUsername().equals(user.getUsername()))
				notifyCaloriesService = false;

			if (StringUtils.isEmpty(entity.getPassword())) {
				entity.setPassword(currentEntity.getPassword());
			} else {
				entity.setPassword(passwordEncoder.encode(entity.getPassword()));
			}
		} else {
			entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		}

		UserEntity savedEntity = userRepository.save(entity);

		//Notify Calories Service that Auth user changed or created
		//TODO: ideally we should just push to message queue
		if (notifyCaloriesService) {
			sendSavedNotification(savedEntity);
		}
		return modelMapper.map(savedEntity, UserDTO.class);
	}

	@Override
	public void deleteUser(Long userId, BaseUser modifier) {
		Optional<UserEntity> optional = userRepository.findById(userId);
		if (optional.isPresent()) {
			UserEntity userEntity = optional.get();
			if (userEntity.getRole().getPriority() < modifier.getRole().getPriority()) {
				throw new UnauthorizedUserException("You can't modify users with higher role");
			}
			userRepository.delete(userEntity);
			sendDeleteNotification(userId);
		}
	}

	@HystrixCommand(fallbackMethod = "onNotifyUserDeleteFail")
	private void sendDeleteNotification(Long userId) {
		WebClient.RequestHeadersSpec request = webClient.delete().uri(notifyUserChangedUrl + "/" + userId);
		request.exchange().subscribe();
	}

	@HystrixCommand(fallbackMethod = "onNotifyUserUpdateFail")
	private void sendSavedNotification(UserEntity savedEntity) {
		WebClient.RequestHeadersSpec request = webClient.post().uri(notifyUserChangedUrl).body(BodyInserters.fromValue(new BaseUser(savedEntity.getId(), savedEntity.getUsername(), savedEntity.getRole())));
		request.exchange().subscribe();
	}

	@Override
	public Page<UserDTO> getUsers(QueryFilteredPageRequest queryFilteredPageRequest) {

		String filter = null;
		if (queryFilteredPageRequest != null) {
			filter = queryFilteredPageRequest.getFilter();
		}

		Pageable pageRequest = pageRequestToPageableConverter.convert(queryFilteredPageRequest, Sort.by("username"));
		Specification specification = queryFilterParser.parse(filter, UserSpecification::new);
		Page<UserEntity> entitiesPage = userRepository.findAll(specification, pageRequest);

		userRepository.findAll(specification, pageRequest);
		Page<UserDTO> result = entitiesPage.map(userEntity -> modelMapper.map(userEntity, UserDTO.class));

		return result;
	}

	@Override
	public UserDTO getUser(Long id) {
		UserEntity userEntity = userRepository.findById(id).orElseThrow();
		return modelMapper.map(userEntity, UserDTO.class);
	}

	private UserDTO onNotifyUserUpdateFail(UserDTO user) {
		logger.warn("Unable to call calories-service (notifyUserChangedUrl) on user save: " + user);
		return null;
	}

	private void onNotifyUserDeleteFail(Long userId) {
		logger.warn("Unable to call calories-service (notifyUserChangedUrl) on user delete. UserId= " + userId);
	}
}
