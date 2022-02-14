package com.digitusforum.module;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.moduleVideo.ModuleVideoEntity;
import com.digitusforum.moduleVideo.ModuleVideoService;
import com.digitusforum.moduleVideo.ModuleVideoVO;
import com.digitusforum.video.VideoEntity;

@RestController
public class ModuleController {
	@Autowired
	ModuleService moduleService;
	@Autowired
	ModuleVideoService moduleVideoService;

	/*
	 * @RequestMapping(value = "/module/v1/retrieveByModule") public
	 * List<ModuleEntity> retrieve(@RequestBody moduleVO moduleVO) { return
	 * moduleService.retrieveByModule(moduleVO); }
	 */

	@RequestMapping(value = "/module/v1/addVideo")
	public ModuleVideoEntity addVideo(@RequestBody ModuleVideoVO moduleVideoVO) {
		return moduleVideoService.addVideoToModule(moduleVideoVO);
	}

	@RequestMapping(value = "/module/v1/reorder")
	public List<ModuleVideoEntity> reorder(@RequestBody ModuleVideoVO moduleVideoVO) {
		return moduleVideoService.reorder(moduleVideoVO);
	}

	@RequestMapping(value = "/module/v1/removeVideo")
	public ModuleVideoVO removeVideoFromModule(@RequestBody ModuleVideoVO moduleVideoVO) {
		return moduleVideoService.removeVideoFromModule(moduleVideoVO);
	}

	@RequestMapping(value = "/module/v1/create")
	public ModuleEntity create(@RequestBody moduleVO moduleVO) {
		return moduleService.create(moduleVO);
	}

	@RequestMapping(value = "/module/v1/delete")
	public moduleVO delete(@RequestBody moduleVO moduleVO) {
		return moduleService.delete(moduleVO);
	}

	// TODO trazer info da triha como nome, descricao etc
	@RequestMapping(value = "/module/v1/retrieveInfo}")
	public String retrieveById(@RequestBody moduleVO moduleVO) {
		return null;// trailService.retrieve();
	}

	// TODO trazer lista de videos do module
	@RequestMapping(value = "/module/v1/retrieveVideos")
	public List<VideoEntity> retrieveVideos(@RequestBody moduleVO moduleVO) {
		return moduleVideoService.retrieveVideos(moduleVO);
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