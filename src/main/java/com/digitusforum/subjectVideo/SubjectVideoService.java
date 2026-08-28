package com.digitusforum.subjectVideo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.training.TrainingRepository;
import com.digitusforum.training.TrainingService;
import com.digitusforum.module.ModuleRepository;
import com.digitusforum.module.ModuleService;
import com.digitusforum.module.ModuleVO;
import com.digitusforum.subject.SubjectEntity;
import com.digitusforum.subject.SubjectRepository;
import com.digitusforum.subject.SubjectVO;
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.VideoService;
import com.digitusforum.video.VideoVO;
import com.digitusforum.video.VideoEntity;

@Service
public class SubjectVideoService {
	//TODO ponderar sobre o serviço ter apenas seu proprio repositorio
	@Autowired
	SubjectVideoRepository subjectVideoRepository;
	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	VideoRepository videoRepository;
	@Autowired
	TrainingRepository trainingRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	TrainingService trainingService;
	@Autowired
	ModuleService moduleService;
	@Autowired
	VideoService videoService;
	RequestService requestService = new RequestService();
	
	public List<SubjectVO> retrieveByVideo(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_VIDEO_ID);
		List<SubjectVO> subjects = new ArrayList<>();
		List<SubjectVideoEntity> subjectVideoEntities = subjectVideoRepository.findByVideoIdOrderByPositionAsc(subjectVO.getVideoId());
		//List<SubjectVideoVO> subjectVideos = new ModelMapper().map(subjectVideoEntities, List.class);
		for (SubjectVideoEntity subjectVideoEntity : subjectVideoEntities) {
			SubjectEntity subjectEntity = subjectRepository.findBySubjectIdAndDeletedIsFalse(subjectVideoEntity.getSubjectId());
			subjects.add(new ModelMapper().map(subjectEntity, SubjectVO.class));
		}
		return subjects;
	}

	public List<SubjectVideoEntity> reorder(SubjectVideoVO subjectVideoVO) {
		if (StringUtils.isBlank(subjectVideoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(subjectVideoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_VIDEO_ID);
		if (StringUtils.isBlank(subjectVideoVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_SUBJECT_ID);
		if (subjectVideoVO.getNewPosition() == 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_NEW_POSITION);

		//moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getModuleId(), moduleVideoVO.getUserId());
		//videoService.checkIfThisVideoBelongToThisUser(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());

		
		
		
		
		
		
		
		SubjectVideoEntity subjectVideoEntity = subjectVideoRepository.findBySubjectIdAndVideoId(subjectVideoVO.getSubjectId(), subjectVideoVO.getVideoId());
		if (subjectVideoEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_NOT_FOUND_IN_SUBJECT);

		subjectVideoEntity.setPosition(subjectVideoVO.getNewPosition());
		fixPositions(subjectVideoVO);
		return subjectVideoRepository.findBySubjectIdOrderByPositionAsc(subjectVideoVO.getSubjectId());
	}

	private void fixPositions(SubjectVideoVO subjectVideoVO) {
		List<SubjectVideoEntity> videos = subjectVideoRepository.findBySubjectIdOrderByPositionAsc(subjectVideoVO.getSubjectId());
		for (int i = 0; i < videos.size(); i++) {
			if (videos.get(i).getPosition() != i) {
				videos.get(i).setPosition(i);
				subjectVideoRepository.save(videos.get(i));
			}
		}
	}

	public SubjectVideoVO removeVideoFromSubject(SubjectVideoVO subjectVideoVO) {
		if (StringUtils.isBlank(subjectVideoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(subjectVideoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_VIDEO_ID);
		if (StringUtils.isBlank(subjectVideoVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_SUBJECT_ID);

		//moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getModuleId(), moduleVideoVO.getUserId());
		//videoService.checkIfThisVideoBelongToThisUser(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());

		SubjectVideoEntity subjectVideoEntity = subjectVideoRepository.findBySubjectIdAndVideoId(subjectVideoVO.getSubjectId(), subjectVideoVO.getVideoId());
		if (subjectVideoEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_NOT_FOUND_IN_SUBJECT);

		subjectVideoRepository.deleteById(subjectVideoEntity.getSubjectVideoId());
		//fixPositions(subjectVideoVO);
		return subjectVideoVO;
	}

	public SubjectVideoVO addVideoToSubject(SubjectVideoVO subjectVideoVO) {
		if (StringUtils.isBlank(subjectVideoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(subjectVideoVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_TRAINING_ID);
		if (StringUtils.isBlank(subjectVideoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_VIDEO_ID);
		if (StringUtils.isBlank(subjectVideoVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_VIDEO_MISSING_SUBJECT_ID);
		
		trainingRepository.findByUserIdAndTrainingIdAndDeletedIsFalse(
				subjectVideoVO.getUserId(),
				subjectVideoVO.getTrainingId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.TRAINING_NOT_FOUND));
		
		videoRepository.findByUserIdAndVideoIdAndDeletedIsFalse(subjectVideoVO.getUserId(), subjectVideoVO.getVideoId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND));
	
		subjectRepository.findByUserIdAndSubjectIdAndDeletedIsFalse(
				subjectVideoVO.getUserId(),
				subjectVideoVO.getSubjectId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.SUBJECT_NOT_FOUND));

		SubjectVideoEntity subjectVideoEntity = subjectVideoRepository.findBySubjectIdAndVideoId(subjectVideoVO.getSubjectId(), subjectVideoVO.getVideoId());
		if (subjectVideoEntity != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_ALREADY_IN_SUBJECT);

		subjectVideoVO.setUserId(null);
		subjectVideoEntity = new ModelMapper().map(subjectVideoVO, SubjectVideoEntity.class);
		subjectVideoEntity = subjectVideoRepository.save(subjectVideoEntity);
		subjectVideoVO = new ModelMapper().map(subjectVideoEntity, SubjectVideoVO.class);
		return subjectVideoVO;
	}

	private int getLastPosition(SubjectVideoVO subjectVideoVO) {
		List<SubjectVideoEntity> videos = subjectVideoRepository.findBySubjectIdOrderByPositionAsc(subjectVideoVO.getSubjectId());
		if (videos.size() == 0)
			return 1;
		return videos.get(videos.size() - 1).getPosition();
	}

	

}
