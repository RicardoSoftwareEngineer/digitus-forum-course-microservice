package com.digitusforum.video;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<VideoEntity, String> {
	//VideoEntity findBySubjectId(String subjectId);
	//List<VideoEntity> findBySubjectIdAndDeletedIsFalse(String subjectId);
	List<VideoEntity> findByDeletedIsFalse();
	VideoEntity findByVideoIdAndDeletedIsFalse(String id);
	Optional<VideoEntity> findByUserIdAndVideoIdAndDeletedIsFalse(String userId, String videoId);
}
