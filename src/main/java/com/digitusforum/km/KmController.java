package com.digitusforum.km;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.kmTree.KmTreeEntity;
import com.digitusforum.kmTree.KmTreeService;
import com.digitusforum.kmTree.KmTreeVO;

@RestController
public class KmController {
	@Autowired
	KmService kmService;
	@Autowired
	KmTreeService kmTreeService;

	@RequestMapping(value = "/km/v1/addTree")
	public KmTreeEntity addTree(@RequestBody KmTreeVO kmTreeVO) {
		return kmTreeService.addTreeToKm(kmTreeVO);
	}
	
	@RequestMapping(value = "/km/v1/reorderTree")
	public List<KmTreeEntity> reorderTree(@RequestBody KmTreeVO kmTreeVO) {
		return kmTreeService.reorder(kmTreeVO);
	}
	
	@RequestMapping(value = "/km/v1/removeTree")
	public KmTreeVO removeTreeFromKm(@RequestBody KmTreeVO kmTreeVO) {
		return kmTreeService.removeTreeFromKm(kmTreeVO);
	}
	
	@RequestMapping(value = "/km/v1/create")
	public KmEntity create(@RequestBody KmVO kmVO) {
		return kmService.create(kmVO);
	}

	@RequestMapping(value = "/km/v1/retrieveByTrail")
	public List<KmEntity> retrieve(@RequestBody KmVO kmVO) {
		return kmService.retrieveByTrail(kmVO);
	}

	@RequestMapping(value = "/km/v1/reorder")
	public List<KmEntity> reorder(@RequestBody KmVO kmVO) {
		return kmService.reorder(kmVO);
	}

	@RequestMapping(value = "/km/v1/delete")
	public KmVO delete(@RequestBody KmVO kmVO) {
		return kmService.delete(kmVO);
	}

	// TODO trazer info da triha como nome, descricao etc
	@RequestMapping(value = "/km/v1/retrieve/{trailId}")
	public String retrieveById() {
		return null;// trailService.retrieve();
	}

	// TODO trazer lista de kms da trilha
	@RequestMapping(value = "/km/v1/retrieve/{trailId}/km")
	public String retrieveKms() {
		return null;// trailService.retrieve();
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