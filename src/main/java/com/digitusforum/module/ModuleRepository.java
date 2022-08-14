package com.digitusforum.module;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ModuleRepository extends CrudRepository<ModuleEntity, String> {
	List<ModuleEntity> findByCourseIdOrderByNumber(String courseId);
	List<ModuleEntity> findByModuleIdOrderByNumber(String moduleId);
	ModuleEntity findByModuleIdAndNumber(String moduleId, int number);
	Optional<ModuleEntity> findByModuleIdAndCourseId(String moduleId, String courseId);
	void deleteById(String id);
}
