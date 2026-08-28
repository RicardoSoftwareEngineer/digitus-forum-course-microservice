package com.digitusforum.subject;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<SubjectEntity, String> {
	List<SubjectEntity> findByTrainingIdAndDeletedIsFalse(String perfilId);

	SubjectEntity findByUserIdAndTrainingIdAndNameAndDeletedIsFalse(String userId, String trainingId, String name);

	SubjectEntity findBySubjectIdAndDeletedIsFalse(String subjectId);
	
	Optional<SubjectEntity> findByUserIdAndSubjectIdAndDeletedIsFalse(String userId, String subjectId);
	
	List<SubjectEntity> findByDeletedIsFalse();

}
