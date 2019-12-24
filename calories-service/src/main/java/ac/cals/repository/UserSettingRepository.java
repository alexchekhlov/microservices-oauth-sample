package ac.cals.repository;

import ac.cals.domain.UserSettingsEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingRepository extends PagingAndSortingRepository<UserSettingsEntity, Long>, JpaSpecificationExecutor<UserSettingsEntity> {
}
