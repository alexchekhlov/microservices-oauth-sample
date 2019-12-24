package ac.system.auth.repository;

import ac.system.auth.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
	Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
