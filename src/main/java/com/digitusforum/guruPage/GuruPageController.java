package com.digitusforum.guruPage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuruPageController {
	@Autowired
	GuruPageService guruPageService;

	@RequestMapping(value = "/guruPage/v1/retrieveByGuruId")
	public List<GuruPageEntity> retrieveByGuruId(@RequestBody GuruPageVO guruPageVO) {
		return guruPageService.retrieveByGuruId(guruPageVO);
	}

}
