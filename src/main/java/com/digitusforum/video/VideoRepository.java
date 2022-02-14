package com.digitusforum.video;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<VideoEntity, String> {
	VideoEntity findBySubjectId(String subjectId);

	List<VideoEntity> findBySubjectIdAndDeletedIsFalse(String subjectId);

	List<VideoEntity> findByDeletedIsFalse();

	VideoEntity findByVideoIdAndDeletedIsFalse(String id);
}
