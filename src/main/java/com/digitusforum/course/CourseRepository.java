package com.digitusforum.course;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<CourseEntity, String> {
	List<CourseEntity> findByPerfilIdAndDeletedIsFalse(String perfilId);
	CourseEntity findByCourseIdAndDeletedIsFalse(String trailId);
	List<CourseEntity> findTop9ByDeletedIsFalse();

	CourseEntity findByUserIdAndPerfilIdAndNameAndDeletedIsFalse(String userId, String perfilId, String name);
}
