package com.digitusforum.moduleVideo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ModuleVideoRepository extends CrudRepository<ModuleVideoEntity, String> {
	List<ModuleVideoEntity> findByUserIdAndModuleIdOrderByPositionAsc(String userId, String moduleId);
	List<ModuleVideoEntity> findByTrainingIdOrderByPositionAsc(String trainingId);
	List<ModuleVideoEntity> findByVideoId(String videoId);
	List<ModuleVideoEntity> findByModuleIdAndPositionGreaterThanEqualOrderByPositionAsc(String moduleId, int position);
	ModuleVideoEntity findByModuleIdAndVideoIdAndUserId(String moduleId, String videoId, String userId);
	Optional<ModuleVideoEntity> findByModuleIdAndVideoId(String moduleId, String videoId);
	ModuleVideoEntity findByModuleIdAndPositionAndUserId(String moduleId, int position, String userId);
	void deleteById(String id);
	void deleteByModuleId(String moduleId);
}
