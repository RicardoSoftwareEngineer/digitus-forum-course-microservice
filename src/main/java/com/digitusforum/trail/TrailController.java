package com.digitusforum.trail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrailController {
	@Autowired
	TrailService trailService;

	@RequestMapping(value = "/trail/v1/create")
	public TrailEntity create(@RequestBody TrailVO trailVO) {
		return trailService.create(trailVO);
	}

	@CrossOrigin
	@RequestMapping(value = "/trail/v1/retrieve")
	public List<TrailEntity> retrieve() {
		return trailService.retrieve();
	}

	@RequestMapping(value = "/trail/v1/retrieveByPerfil")
	public List<TrailEntity> retrieve(@RequestBody TrailVO trailVO) {
		return trailService.retrieveByPerfil(trailVO);
	}

	// TODO trazer info da triha como nome, descricao etc
	@RequestMapping(value = "/trail/v1/retrieveById")
	public TrailEntity retrieveById(@RequestBody TrailVO trailVO) {
		return trailService.retrieveById(trailVO);
	}

	// TODO AAA trazer lista de kms da trilha
	@RequestMapping(value = "/trail/v1/retrieve/{trailId}/km")
	public String retrieveKms(@RequestBody TrailVO trailVO) {
		return trailService.retrieveKms(trailVO);
	}

	/*
	 * @RequestMapping(value = "/user/v1/{id}/retrieve") public Object
	 * retrieveById(@PathVariable String id) { return trailService.retrieveById(id);
	 * }
	 * 
	 * @RequestMapping(value = "/user/v1/retrieve/byEmailAndPassword") public
	 * TrailVO retrieve(@RequestBody TrailVO user) { return
	 * trailService.retrieveByEmailAndPassword(user); }
	 * 
	 * @RequestMapping(value = "/user/v1/{id}/update") public Object
	 * update(@PathVariable String id, @RequestBody TrailVO user) { return
	 * trailService.update(user, id); }
	 * 
	 * @RequestMapping(value = "/user/v1/{id}/delete") public Object
	 * delete(@PathVariable String id) { return trailService.delete(id); }
	 */
}