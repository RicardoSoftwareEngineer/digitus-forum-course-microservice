package com.digitusforum.link;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LinkRepository extends CrudRepository<LinkEntity, String> {
	List<LinkEntity> findByVideoId(String videoId);

}
