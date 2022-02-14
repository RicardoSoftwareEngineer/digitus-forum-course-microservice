package com.digitusforum.subject;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<SubjectEntity, String> {
	List<SubjectEntity> findByPerfilIdAndDeletedIsFalse(String perfilId);

	SubjectEntity findByPerfilIdAndNameAndDeletedIsFalse(String perfilId, String name);

	SubjectEntity findBySubjectIdAndDeletedIsFalse(String subjectId);
	
	List<SubjectEntity> findByDeletedIsFalse();

}
