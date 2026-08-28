package com.digitusforum.training;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TrainingRepository extends CrudRepository<TrainingEntity, String> {
	List<TrainingEntity> findByPerfilIdAndDeletedIsFalse(String perfilId);
	TrainingEntity findByTrainingIdAndDeletedIsFalse(String trainingId);
	List<TrainingEntity> findTop9ByDeletedIsFalse();

	TrainingEntity findByUserIdAndPerfilIdAndNameAndDeletedIsFalse(String userId, String perfilId, String name);
	
	Optional<TrainingEntity> findByUserIdAndTrainingIdAndDeletedIsFalse(String userId, String trainingId);
}
