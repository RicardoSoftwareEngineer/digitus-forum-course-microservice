package com.digitusforum.video;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.moduleVideo.ModuleVideoService;

@RestController
public class VideoController {
	@Autowired
	VideoService videoService;
	@Autowired
	ModuleVideoService moduleVideoService;

	@RequestMapping(value = "/video/v1/create")
	public VideoVO create(@RequestBody VideoVO videoVO) {
		return videoService.create(videoVO);
	}

	@RequestMapping(value = "/video/v1/retrieveById")
	public VideoVO retrieveById(@RequestBody VideoVO videoVO) {
		return videoService.retrieveById(videoVO);
	}

	@RequestMapping(value = "/video/v1/retrieveBySubjectId")
	public List<VideoVO> retrieve(@RequestBody VideoVO videoVO) {
		return videoService.retrieveBySubject(videoVO);
	}

	@RequestMapping(value = "/video/v1/update")
	public VideoVO update(@RequestBody VideoVO videoVO) {
		return videoService.update(videoVO);
	}
	
	@RequestMapping(value = "/video/v1/delete")
	public VideoVO delete(@RequestBody VideoVO videoVO) {
		return videoService.delete(videoVO);
	}

}