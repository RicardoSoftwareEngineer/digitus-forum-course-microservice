package com.digitusforum.subject;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.course.CourseEntity;
import com.digitusforum.course.CourseRepository;
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoEntity;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.VideoService;
import com.digitusforum.video.VideoVO;

@Service
public class SubjectService {

	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	VideoRepository videoRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	VideoService videoService;
	RequestService requestService = new RequestService();

	public SubjectVO create(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_USER_ID);
		if (StringUtils.isBlank(subjectVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_NAME);
		if (StringUtils.isBlank(subjectVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_COURSE_ID);

		courseRepository.findByUserIdAndCourseIdAndDeletedIsFalse(
				subjectVO.getUserId(),
				subjectVO.getCourseId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.COURSE_NOT_FOUND));

		SubjectEntity subjectFromDB = subjectRepository.findByUserIdAndCourseIdAndNameAndDeletedIsFalse(
				subjectVO.getUserId(),
				subjectVO.getCourseId(),
				subjectVO.getName());
		if (subjectFromDB != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_NAME_ALREADY_IN_USE);

		SubjectEntity subjectEntity = new ModelMapper().map(subjectVO, SubjectEntity.class);
		subjectEntity = subjectRepository.save(subjectEntity);
		subjectVO = new ModelMapper().map(subjectEntity, SubjectVO.class);
		return subjectVO;
	}

	public List<SubjectVO> retrieveByCourseId(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_COURSE_ID);

		List<SubjectEntity> subjectEntities = subjectRepository
				.findByCourseIdAndDeletedIsFalse(subjectVO.getCourseId());
		List<SubjectVO> subjects = new ModelMapper().map(subjectEntities, List.class);
		return subjects;
	}

	public List<SubjectEntity> retrieveByCourse(SubjectVO subjectVO) {
		return subjectRepository.findByCourseIdAndDeletedIsFalse(subjectVO.getCourseId());
	}

	public SubjectVO retrieveByIdWithVideos(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_ID);

		SubjectEntity subjectEntity = subjectRepository.findBySubjectIdAndDeletedIsFalse(subjectVO.getSubjectId());
		if (subjectEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.SUBJECT_NOT_FOUND);

		String userId = subjectVO.getUserId();
		subjectVO = new ModelMapper().map(subjectEntity, SubjectVO.class);
		subjectVO.setUserId(userId);
		//List<VideoEntity> videoEntities = videoRepository.findBySubjectIdAndDeletedIsFalse(subjectVO.getSubjectId());
		//List<VideoVO> videos = new ModelMapper().map(videoEntities, List.class);
		subjectVO.setVideos(videoService.retrieveBySubject(subjectVO));
		return subjectVO;
	}
	
	
	
	public SubjectVO update(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_ID);
		
		SubjectEntity subjectEntity = subjectRepository.findById(subjectVO.getSubjectId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.SUBJECT_NOT_FOUND));
		
		if (StringUtils.isNotBlank(subjectVO.getName()))
			subjectEntity.setName(subjectVO.getName());
		if (StringUtils.isNotBlank(subjectVO.getSinopse()))
			subjectEntity.setSinopse(subjectVO.getSinopse());
		if (StringUtils.isNotBlank(subjectVO.getDescription()))
			subjectEntity.setDescription(subjectVO.getDescription());
		
		subjectRepository.save(subjectEntity);
		subjectVO = new ModelMapper().map(subjectEntity, SubjectVO.class);
		return subjectVO;
	}

	

	

}
