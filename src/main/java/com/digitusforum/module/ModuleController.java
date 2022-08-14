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
	// TODO $$$ continuar criando o crud
	@RequestMapping(value = "/module/v1/create")
	public ModuleVO create(@RequestBody ModuleVO moduleVO) {
		return moduleService.create(moduleVO);
	}

	@RequestMapping(value = "/module/v1/retrieveById")
	public ModuleVO retrieveById(@RequestBody ModuleVO moduleVO) {
		return moduleService.retrieveById(moduleVO);
	}

	@RequestMapping(value = "/module/v1/retrieveByCourseId")
	public List<ModuleVO> retrieveByCourseId(@RequestBody ModuleVO moduleVO) {
		return moduleService.retrieveByCourseId(moduleVO);
	}

	@RequestMapping(value = "/module/v1/retrieveByCourseIdWithVideos")
	public List<ModuleVO> retrieveByCourseWithVideos(@RequestBody ModuleVO moduleVO) {
		return moduleService.retrieveByCourseWithVideos(moduleVO);
	}

	@RequestMapping(value = "/module/v1/update")
	public ModuleVO update(@RequestBody ModuleVO moduleVO) {
		return moduleService.update(moduleVO);
	}

	@RequestMapping(value = "/module/v1/delete")
	public ModuleVO delete(@RequestBody ModuleVO moduleVO) {
		return moduleService.delete(moduleVO);
	}

	@RequestMapping(value = "/module/v1/addVideo")
	public ModuleVideoVO addVideo(@RequestBody ModuleVideoVO moduleVideoVO) {
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

	
}