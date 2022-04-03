package com.digitusforum.moduleVideo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ModuleVideoRepository extends CrudRepository<ModuleVideoEntity, String> {
	List<ModuleVideoEntity> findByModuleIdOrderByPositionAsc(String moduleId);
	List<ModuleVideoEntity> findByCourseIdOrderByPositionAsc(String courseId);
	List<ModuleVideoEntity> findByVideoId(String videoId);
	ModuleVideoEntity findByModuleIdAndVideoId(String moduleId, String videoId);
	void deleteById(String id);
	void deleteByModuleId(String moduleId);
}
