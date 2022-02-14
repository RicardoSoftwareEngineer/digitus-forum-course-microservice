package com.digitusforum.moduleVideo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KmTreeController {
	@Autowired
	ModuleVideoService kmService;

	
	
	/*
	@RequestMapping(value = "/user/v1/{id}/retrieve")
	public Object retrieveById(@PathVariable String id) {
		return trailService.retrieveById(id);
	}

	@RequestMapping(value = "/user/v1/retrieve/byEmailAndPassword")
	public TrailVO retrieve(@RequestBody TrailVO user) {
		return trailService.retrieveByEmailAndPassword(user);
	}

	@RequestMapping(value = "/user/v1/{id}/update")
	public Object update(@PathVariable String id, @RequestBody TrailVO user) {
		return trailService.update(user, id);
	}

	@RequestMapping(value = "/user/v1/{id}/delete")
	public Object delete(@PathVariable String id) {
		return trailService.delete(id);
	}
	*/
}