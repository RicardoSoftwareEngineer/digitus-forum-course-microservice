package com.digitusforum.module;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ModuleRepository extends CrudRepository<ModuleEntity, String> {
	List<ModuleEntity> findByCourseIdOrderByNumber(String moduleId);
	List<ModuleEntity> findByModuleIdOrderByNumber(String moduleId);
	ModuleEntity findByModuleIdAndNumber(String moduleId, int number);
	void deleteById(String id);
}
