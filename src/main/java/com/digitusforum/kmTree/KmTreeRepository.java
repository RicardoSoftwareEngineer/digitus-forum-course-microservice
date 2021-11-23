package com.digitusforum.kmTree;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface KmTreeRepository extends CrudRepository<KmTreeEntity, String> {
	List<KmTreeEntity> findByKmIdOrderByPositionAsc(String kmId);
	List<KmTreeEntity> findByTreeId(String treeId);
	KmTreeEntity findByKmIdAndTreeId(String kmId, String treeId);
	void deleteById(String id);
}
