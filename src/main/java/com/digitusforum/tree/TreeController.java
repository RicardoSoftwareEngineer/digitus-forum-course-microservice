package com.digitusforum.tree;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.kmTree.KmTreeEntity;
import com.digitusforum.kmTree.KmTreeService;
import com.digitusforum.kmTree.KmTreeVO;

@RestController
public class TreeController {
	@Autowired
	TreeService treeService;
	@Autowired
	KmTreeService kmTreeService;

	//TODO transferir arvore entre kms
	
	@RequestMapping(value = "/tree/v1/reorder")
	public List<KmTreeEntity> reorder(@RequestBody KmTreeVO kmTreeVO) {
		return kmTreeService.reorder(kmTreeVO);
	}
	
	@RequestMapping(value = "/tree/v1/removeTreeFromKm")
	public KmTreeVO removeTreeFromKm(@RequestBody KmTreeVO kmTreeVO) {
		return kmTreeService.removeTreeFromKm(kmTreeVO);
	}

	@RequestMapping(value = "/tree/v1/addTreeToKm")
	public KmTreeEntity addTreeToKm(@RequestBody KmTreeVO kmTreeVO) {
		return kmTreeService.addTreeToKm(kmTreeVO);
	}
	
	@RequestMapping(value = "/tree/v1/create")
	public TreeEntity create(@RequestBody TreeVO treeVO) {
		return treeService.create(treeVO);
	}

	@RequestMapping(value = "/tree/v1/retrieveByForest")
	public List<TreeEntity> retrieve(@RequestBody TreeVO treeVO) {
		return treeService.retrieveByForest(treeVO);
	}

	@RequestMapping(value = "/tree/v1/retrieveById")
	public TreeEntity retrieveById(@RequestBody TreeVO treeVO) {
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