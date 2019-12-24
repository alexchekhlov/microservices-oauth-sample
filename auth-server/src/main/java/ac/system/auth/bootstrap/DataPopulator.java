package ac.system.auth.bootstrap;

import ac.system.auth.domain.UserEntity;
import ac.system.auth.repository.UserRepository;
import ac.common.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DataPopulator implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		List users = new ArrayList();

		users.add(new UserEntity("admin", passwordEncoder.encode("admin"), UserRole.ADMIN));
		users.add(new UserEntity("mgr", passwordEncoder.encode("manager"), UserRole.MANAGER));

		List<UserEntity> list = IntStream.range(1, 11)
				.mapToObj(ind -> new UserEntity("user" + ind, passwordEncoder.encode("user"), UserRole.USER))
				.collect(Collectors.toList());

		users.addAll(list);

		userRepository.saveAll(users);
	}
}
