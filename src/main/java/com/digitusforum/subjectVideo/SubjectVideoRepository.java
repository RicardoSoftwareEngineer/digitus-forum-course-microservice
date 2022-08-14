package com.digitusforum.subjectVideo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SubjectVideoRepository extends CrudRepository<SubjectVideoEntity, String> {
	List<SubjectVideoEntity> findBySubjectIdOrderByPositionAsc(String subjectId);
	List<SubjectVideoEntity> findByVideoIdOrderByPositionAsc(String videoId);
	List<SubjectVideoEntity> findByCourseIdOrderByPositionAsc(String courseId);
	//List<SubjectVideoEntity> findByVideoId(String videoId);
	SubjectVideoEntity findBySubjectIdAndVideoId(String subjectId, String videoId);
	void deleteById(String id);
	void deleteBySubjectId(String subjectId);
}
