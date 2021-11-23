package com.digitusforum.tree;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface TreeRepository extends CrudRepository<TreeEntity, String> {
	TreeEntity findByForestId(String forestId);

	List<TreeEntity> findByForestIdAndDeletedIsFalse(String forestId);

	List<TreeEntity> findByDeletedIsFalse();

	TreeEntity findByTreeIdAndDeletedIsFalse(String id);
}
