package com.digitusforum.km;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface KmRepository extends CrudRepository<KmEntity, String> {
	List<KmEntity> findByTrailIdOrderByNumber(String trailId);
	KmEntity findByTrailIdAndNumber(String trailId, int number);
	void deleteById(String id);
}
