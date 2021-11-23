package com.digitusforum.forest;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ForestRepository extends CrudRepository<ForestEntity, String> {
	List<ForestEntity> findByPerfilIdAndDeletedIsFalse(String perfilId);

	ForestEntity findByPerfilIdAndNameAndDeletedIsFalse(String perfilId, String name);

	ForestEntity findByForestIdAndDeletedIsFalse(String forestId);
	
	List<ForestEntity> findByDeletedIsFalse();

}
