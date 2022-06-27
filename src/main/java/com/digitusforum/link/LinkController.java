package com.digitusforum.link;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkController {
	@Autowired
	LinkService linkService;

	@RequestMapping(value = "/link/v1/create")
	public LinkVO create(@RequestBody LinkVO linkVO) {
		return linkService.create(linkVO);
	}

	@RequestMapping(value = "/link/v1/retrieveByVideoId")
	public List<LinkVO> retrieveById(@RequestBody LinkVO linkVO) {
		return linkService.retrieveByVideoId(linkVO);
	}

	@RequestMapping(value = "/link/v1/update")
	public LinkVO update(@RequestBody LinkVO linkVO) {
		return linkService.update(linkVO);
	}

	@RequestMapping(value = "/link/v1/delete")
	public LinkVO delete(@RequestBody LinkVO linkVO) {
		return linkService.delete(linkVO);
	}

}