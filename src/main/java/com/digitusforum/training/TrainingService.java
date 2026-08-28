package com.digitusforum.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.module.ModuleEntity;
import com.digitusforum.module.ModuleRepository;
import com.digitusforum.module.ModuleVO;
import com.digitusforum.moduleVideo.ModuleVideoEntity;
import com.digitusforum.moduleVideo.ModuleVideoRepository;
import com.digitusforum.subject.SubjectEntity;
import com.digitusforum.subject.SubjectRepository;
import com.digitusforum.subject.SubjectVO;
import com.digitusforum.subjectVideo.SubjectVideoEntity;
import com.digitusforum.subjectVideo.SubjectVideoRepository;
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoEntity;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.VideoVO;

@Service
public class TrainingService {

	@Autowired
	TrainingRepository trainingRepository;
	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	VideoRepository videoRepository;
	@Autowired
	ModuleVideoRepository moduleVideoRepository;
	@Autowired
	SubjectVideoRepository subjectVideoRepository;
	RequestService requestService = new RequestService();

	public List<ModuleVO> retrieveModulesWithVideosByTrainingIdDEPRECATED(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_TRAINING_ID);

		// List<ModuleEntity> modulesEntity =
		// moduleRepository.findByTrainingIdOrderByNumber(trainingVO.getTrainingId());
		List<ModuleVO> modules = moduleRepository.findByTrainingIdOrderByNumber(trainingVO.getTrainingId()).stream()
				.map(module -> new ModelMapper().map(module, ModuleVO.class)).collect(Collectors.toList());
		List<VideoVO> videos = moduleVideoRepository.findByTrainingIdOrderByPositionAsc(trainingVO.getTrainingId()).stream()
				.map(video -> new ModelMapper().map(video, VideoVO.class)).collect(Collectors.toList());

		for (VideoVO video : videos)
			for (ModuleVO module : modules)
				if (video.getModuleId().equals(module.getModuleId()))
					module.getVideos().add(video);

		return modules;
	}

	public List<ModuleVideoEntity> retrieveVideosByTrainingId(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_TRAINING_ID);

		return moduleVideoRepository.findByTrainingIdOrderByPositionAsc(trainingVO.getTrainingId());
	}

	public boolean checkIfThisTrainingBelongToThisUser(String trainingId, String userId) {
		TrainingEntity training = trainingRepository.findByTrainingIdAndDeletedIsFalse(trainingId);
		if (training == null || StringUtils.isBlank(userId) || !userId.equals(training.getUserId()))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.TRAINING_NOT_FOUND);
		return true;
	}

	public List<ModuleEntity> retrieveModulesByTrainingId(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_ID);

		return moduleRepository.findByTrainingIdOrderByNumber(trainingVO.getTrainingId());
	}

	public TrainingEntity create(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_USER_ID);
		if (StringUtils.isBlank(trainingVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(trainingVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_NAME);

		TrainingEntity trainingFromDB = trainingRepository.findByUserIdAndPerfilIdAndNameAndDeletedIsFalse(
				trainingVO.getUserId(), trainingVO.getPerfilId(), trainingVO.getName());
		if (trainingFromDB != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_NAME_ALREADY_IN_USE);

		TrainingEntity trainingEntity = new ModelMapper().map(trainingVO, TrainingEntity.class);
		trainingEntity = trainingRepository.save(trainingEntity);
		return trainingEntity;
	}

	public List<TrainingEntity> retrieveByPerfil(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_USER_ID);
		requestService.checkIfThisPerfilBelongsToThisUser(trainingVO.getPerfilId(), trainingVO.getUserId());
		return trainingRepository.findByPerfilIdAndDeletedIsFalse(trainingVO.getPerfilId());
	}

	public List<TrainingEntity> retrieveByForest(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_USER_ID);
		return null;
	}

	public TrainingEntity retrieveById(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_USER_ID);
		if (StringUtils.isBlank(trainingVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_ID);
		checkIfThisTrainingBelongToThisUser(trainingVO.getTrainingId(), trainingVO.getUserId());
		return trainingRepository.findByTrainingIdAndDeletedIsFalse(trainingVO.getTrainingId());
	}

	public List<TrainingEntity> retrieveAll() {
		return trainingRepository.findTop9ByDeletedIsFalse();
	}

	public TrainingVO delete(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_USER_ID);
		if (StringUtils.isBlank(trainingVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(trainingVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_ID);

		requestService.checkIfThisPerfilBelongsToThisUser(trainingVO.getPerfilId(), trainingVO.getUserId());
		checkIfThisTrainingBelongToThisUser(trainingVO.getTrainingId(), trainingVO.getUserId());

		TrainingEntity trainingFromDB = trainingRepository.findByTrainingIdAndDeletedIsFalse(trainingVO.getTrainingId());
		if (trainingFromDB == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_NOT_FOUND);

		trainingRepository.deleteById(trainingVO.getTrainingId());
		return trainingVO;
	}
	
	public TrainingVO retrieveSubjectsByTrainingId(TrainingVO trainingVO) {
		if (StringUtils.isBlank(trainingVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAINING_MISSING_ID);
		int count = 1;
		List<SubjectVideoEntity> subjectVideoEntities = subjectVideoRepository.findByTrainingIdOrderByPositionAsc(trainingVO.getTrainingId());
		for (SubjectVideoEntity subjectVideoEntity : subjectVideoEntities) {
			SubjectEntity subjectEntitiy = subjectRepository.findBySubjectIdAndDeletedIsFalse(subjectVideoEntity.getSubjectId());
			if(subjectEntitiy == null)
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_NOT_FOUND + " sup");
			SubjectVO subject = new ModelMapper().map(subjectEntitiy, SubjectVO.class);
			if(!trainingVO.getSubjects().contains(subject))
				trainingVO.getSubjects().add(subject);
		}
		return trainingVO;
	}

	

}
