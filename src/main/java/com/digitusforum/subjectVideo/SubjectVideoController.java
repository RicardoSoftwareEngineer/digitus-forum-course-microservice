package com.digitusforum.subjectVideo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubjectVideoController {
	@Autowired
	SubjectVideoService subjectService;

	
}