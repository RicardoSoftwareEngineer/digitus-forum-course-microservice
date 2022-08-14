package com.digitusforum.subject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.subjectVideo.SubjectVideoService;
import com.digitusforum.subjectVideo.SubjectVideoVO;

@RestController
public class SubjectController {
	@Autowired
	SubjectService subjectService;

	@Autowired
	SubjectVideoService subjectVideoService;

	@RequestMapping(value = "/subject/v1/create")
	public SubjectVO create(@RequestBody SubjectVO subjectVO) {
		return subjectService.create(subjectVO);
	}

	@RequestMapping(value = "/subject/v1/retrieveByCourseId")
	public List<SubjectVO> retrieveByCourseId(@RequestBody SubjectVO subjectVO) {
		return subjectService.retrieveByCourseId(subjectVO);
	}

	@RequestMapping(value = "/subject/v1/retrieveByIdWithVideos")
	public SubjectVO retrieveByIdWithVideos(@RequestBody SubjectVO subjectVO) {
		return subjectService.retrieveByIdWithVideos(subjectVO);
	}

	@RequestMapping(value = "/subject/v1/retrieveByVideo")
	public List<SubjectVO> retrieveByVideo(@RequestBody SubjectVO subjectVO) {
		return subjectVideoService.retrieveByVideo(subjectVO);
	}

	@RequestMapping(value = "/subject/v1/update")
	public SubjectVO update(@RequestBody SubjectVO subjectVO) {
		return subjectService.update(subjectVO);
	}

	@RequestMapping(value = "/subject/v1/addVideo")
	public SubjectVideoVO addVideo(@RequestBody SubjectVideoVO subjectVideoVO) {
		return subjectVideoService.addVideoToSubject(subjectVideoVO);
	}

	@RequestMapping(value = "/subject/v1/removeVideo")
	public SubjectVideoVO removeVideoFromModule(@RequestBody SubjectVideoVO subjectVideoVO) {
		return subjectVideoService.removeVideoFromSubject(subjectVideoVO);
	}

}