package com.digitusforum.video;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.moduleVideo.ModuleVideoEntity;
import com.digitusforum.moduleVideo.ModuleVideoService;
import com.digitusforum.moduleVideo.ModuleVideoVO;

@RestController
public class TreeController {
	@Autowired
	TreeService treeService;
	@Autowired
	ModuleVideoService kmTreeService;

	//TODO transferir arvore entre kms
	
	@RequestMapping(value = "/tree/v1/reorder")
	public List<ModuleVideoEntity> reorder(@RequestBody ModuleVideoVO kmTreeVO) {
		return kmTreeService.reorder(kmTreeVO);
	}
	
	@RequestMapping(value = "/tree/v1/removeTreeFromKm")
	public ModuleVideoVO removeTreeFromKm(@RequestBody ModuleVideoVO kmTreeVO) {
		return kmTreeService.removeVideoFromModule(kmTreeVO);
	}

	@RequestMapping(value = "/tree/v1/addTreeToKm")
	public ModuleVideoEntity addTreeToKm(@RequestBody ModuleVideoVO kmTreeVO) {
		return kmTreeService.addVideoToModule(kmTreeVO);
	}
	
	@RequestMapping(value = "/tree/v1/create")
	public VideoEntity create(@RequestBody TreeVO treeVO) {
		return treeService.create(treeVO);
	}

	@RequestMapping(value = "/tree/v1/retrieveByForest")
	public List<VideoEntity> retrieve(@RequestBody TreeVO treeVO) {
		return treeService.retrieveByForest(treeVO);
	}

	@RequestMapping(value = "/tree/v1/retrieveById")
	public VideoEntity retrieveById(@RequestBody TreeVO treeVO) {
		return treeService.retrieveById(treeVO);
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