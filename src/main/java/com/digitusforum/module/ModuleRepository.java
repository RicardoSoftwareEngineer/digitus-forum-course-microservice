package com.digitusforum.module;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ModuleRepository extends CrudRepository<ModuleEntity, String> {
	List<ModuleEntity> findByTrainingIdOrderByNumber(String trainingId);
	List<ModuleEntity> findByModuleIdOrderByNumber(String moduleId);
	ModuleEntity findByModuleIdAndNumber(String moduleId, int number);
	Optional<ModuleEntity> findByModuleIdAndTrainingId(String moduleId, String trainingId);
	void deleteById(String id);
}
