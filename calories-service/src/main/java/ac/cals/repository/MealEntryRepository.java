package ac.cals.repository;

import ac.cals.domain.MealEntryEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealEntryRepository extends PagingAndSortingRepository<MealEntryEntity, Long>, JpaSpecificationExecutor<MealEntryEntity> {
	@Modifying
	@Query("Delete from MealEntryEntity where user.id =?1")
	void deleteAllByUserId(Long userId);
}
