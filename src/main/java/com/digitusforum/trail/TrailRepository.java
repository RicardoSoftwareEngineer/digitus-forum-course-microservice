package com.digitusforum.trail;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface TrailRepository extends CrudRepository<TrailEntity, String> {
	List<TrailEntity> findByPerfilIdAndDeletedIsFalse(String perfilId);
	TrailEntity findByTrailIdAndDeletedIsFalse(String trailId);
	List<TrailEntity> findByDeletedIsFalse();

	TrailEntity findByUserIdAndPerfilIdAndNameAndDeletedIsFalse(String userId, String perfilId, String name);
}
