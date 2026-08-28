package com.digitusforum.guruPage;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GuruPageRepository extends CrudRepository<GuruPageEntity, String> {
	List<GuruPageEntity> findByGuruIdAndDeletedIsFalseOrderByPositionAsc(String guruId);
}
