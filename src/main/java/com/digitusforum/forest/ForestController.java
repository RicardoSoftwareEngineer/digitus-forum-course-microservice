package com.digitusforum.forest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForestController {
	@Autowired
	ForestService forestService;

	@RequestMapping(value = "/forest/v1/create")
	public ForestEntity create(@RequestBody ForestVO forestVO) {
		return forestService.create(forestVO);
	}

	@RequestMapping(value = "/forest/v1/retrieve")
	public List<ForestEntity> retrieve() {
		return forestService.retrieve();
	}

	@RequestMapping(value = "/forest/v1/retrieveByPerfil")
	public List<ForestEntity> retrieveByPerfil(@RequestBody ForestVO forestVO) {
		return forestService.retrieveByPerfil(forestVO);
	}

	@RequestMapping(value = "/forest/v1/retrieveById")
	public ForestEntity retrieveById(@RequestBody ForestVO forestVO) {
		return forestService.retrieveById(forestVO);
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