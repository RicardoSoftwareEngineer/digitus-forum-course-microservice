package com.digitusforum.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.training.TrainingEntity;
import com.digitusforum.training.TrainingRepository;
import com.digitusforum.training.TrainingService;
import com.digitusforum.link.LinkEntity;
import com.digitusforum.link.LinkRepository;
import com.digitusforum.link.LinkVO;
import com.digitusforum.moduleVideo.ModuleVideoEntity;
import com.digitusforum.moduleVideo.ModuleVideoRepository;
import com.digitusforum.moduleVideo.ModuleVideoVO;
import com.digitusforum.subject.SubjectEntity;
import com.digitusforum.subject.SubjectRepository;
import com.digitusforum.subject.SubjectVO;
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoEntity;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.VideoVO;

@Service
public class ModuleService {

	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	VideoRepository videoRepository;
	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	ModuleVideoRepository moduleVideoRepository;
	@Autowired
	TrainingRepository trainingRepository;
	@Autowired
	LinkRepository linkRepository;
	@Autowired
	TrainingService trainingService;
	RequestService requestService = new RequestService();

	public ModuleVO create(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_TRAINING_ID);
		if (StringUtils.isBlank(moduleVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_NAME);

		trainingRepository.findByUserIdAndTrainingIdAndDeletedIsFalse(moduleVO.getUserId(), moduleVO.getTrainingId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.TRAINING_NOT_FOUND));
		
		moduleVO.setNumber(getLastModule(moduleVO));
		ModuleEntity moduleEntity = new ModelMapper().map(moduleVO, ModuleEntity.class);
		moduleEntity = moduleRepository.save(moduleEntity);
		moduleVO = new ModelMapper().map(moduleEntity, ModuleVO.class);
		return moduleVO;
	}

	public ModuleVO retrieveById(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_ID);
		ModuleEntity moduleEntity = moduleRepository.findById(moduleVO.getModuleId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND));
		moduleVO = new ModelMapper().map(moduleEntity, ModuleVO.class);
		return moduleVO;
	}

	public List<ModuleVO> retrieveByTrainingId(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_TRAINING_ID);

		List<ModuleEntity> moduleEntities = moduleRepository.findByTrainingIdOrderByNumber(moduleVO.getTrainingId());
		List<ModuleVO> modules = new ModelMapper().map(moduleEntities, List.class);
		return modules;
	}

	public List<ModuleVO> retrieveByTrainingWithVideos(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_TRAINING_ID);

		List<ModuleEntity> moduleEntities = moduleRepository.findByTrainingIdOrderByNumber(moduleVO.getTrainingId());
		List<ModuleVO> modules = new ArrayList<>();
		for (ModuleEntity moduleEntity : moduleEntities)
			modules.add(new ModelMapper().map(moduleEntity, ModuleVO.class));

		List<ModuleVideoEntity> moduleVideoEntities = moduleVideoRepository.findByTrainingIdOrderByPositionAsc(moduleVO.getTrainingId());
		List<VideoVO> videos = new ArrayList<>();
		for (ModuleVideoEntity moduleVideoEntity : moduleVideoEntities) {
			VideoEntity videoEntity = videoRepository.findById(moduleVideoEntity.getVideoId()).get();
			VideoVO video = new ModelMapper().map(videoEntity, VideoVO.class);
			video.setModuleId(moduleVideoEntity.getModuleId());
			video.setModuleVideoId(moduleVideoEntity.getModuleVideoId());
			List<LinkEntity> linkEntities = linkRepository.findByVideoId(moduleVideoEntity.getModuleVideoId());
			List<LinkVO> links = new ModelMapper().map(linkEntities, List.class);
			video.setLinks(links);
			videos.add(video);
		}

		for (VideoVO video : videos)
			for (ModuleVO module : modules)
				if (video.getModuleId().equals(module.getModuleId()))
					module.getVideos().add(video);

		return modules;
	}

	public ModuleVO update(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_ID);

		ModuleEntity moduleEntity = moduleRepository.findById(moduleVO.getModuleId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND));

		if (StringUtils.isNotBlank(moduleVO.getName()))
			moduleEntity.setName(moduleVO.getName());
		if (StringUtils.isNotBlank(moduleVO.getSinopse()))
			moduleEntity.setSinopse(moduleVO.getSinopse());
		if (StringUtils.isNotBlank(moduleVO.getDescription()))
			moduleEntity.setDescription(moduleVO.getDescription());
		moduleRepository.save(moduleEntity);
		moduleVO = new ModelMapper().map(moduleEntity, ModuleVO.class);
		return moduleVO;
	}

	public ModuleVO delete(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_ID);
		if (StringUtils.isBlank(moduleVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_TRAINING_ID);

		trainingService.checkIfThisTrainingBelongToThisUser(moduleVO.getTrainingId(), moduleVO.getUserId());

		moduleRepository.findById(moduleVO.getModuleId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND));

		/*
		 * List<ModuleVideoEntity> moduleVideos =
		 * moduleVideoRepository.findByModuleIdOrderByPositionAsc(moduleVO.getModuleId()
		 * ); for (ModuleVideoEntity video : moduleVideos)
		 * moduleVideoRepository.deleteById(video.getModuleId());
		 */
		moduleVideoRepository.deleteByModuleId(moduleVO.getModuleId());
		moduleRepository.deleteById(moduleVO.getModuleId());
		fixNumbers(moduleVO);
		return moduleVO;
	}

	public List<ModuleEntity> reorder(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVO.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_TRAINING_ID);
		if (StringUtils.isBlank(moduleVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_ID);
		if (moduleVO.getNewNumber() == 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_NEW_NUMBER);

		checkIfThisModuleBelongToThisUser(moduleVO.getModuleId(), moduleVO.getUserId());
		trainingService.checkIfThisTrainingBelongToThisUser(moduleVO.getTrainingId(), moduleVO.getUserId());

		Optional<ModuleEntity> moduleEntity = moduleRepository.findById(moduleVO.getModuleId());
		if (moduleEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_NOT_FOUND);
		moduleEntity.get().setNumber(moduleVO.getNewNumber());
		moduleRepository.save(moduleEntity.get());
		fixNumbers(moduleVO);
		List<ModuleEntity> moduleEntities = new ArrayList<>(); // retrieveByModule(moduleVO);
		return moduleEntities;
	}

	public void checkIfThisModuleBelongToThisUser(String moduleId, String userId) {
		Optional<ModuleEntity> moduleEntity = moduleRepository.findById(moduleId);
		if (moduleEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND);
		Optional<TrainingEntity> trainingEntity = trainingRepository.findById(moduleEntity.get().getTrainingId());
		requestService.checkIfThisPerfilBelongsToThisUser(trainingEntity.get().getPerfilId(), userId);
	}

	private void fixNumbers(ModuleVO moduleVO) {
		List<ModuleEntity> modules = moduleRepository.findByTrainingIdOrderByNumber(moduleVO.getTrainingId());
		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).getNumber() != i + 1) {
				modules.get(i).setNumber(i + 1);
				moduleRepository.save(modules.get(i));
			}
		}
	}

	private int getLastModule(ModuleVO moduleVO) {
		List<ModuleEntity> modules = moduleRepository.findByTrainingIdOrderByNumber(moduleVO.getTrainingId());
		if (modules.size() == 0)
			return 1;
		return modules.get(modules.size() - 1).getNumber() + 1;
	}

	/*
	 * public List<ModuleEntity> retrieveByModule(moduleVO moduleVO) {
	 * checkUserId(moduleVO.getUserId()); checkModuleId(moduleVO.getModuleId());
	 * trainingRepository.findById(moduleVO.getTrainingId()) .orElseThrow(() -> new
	 * ResponseStatusException(HttpStatus.NOT_FOUND, M.TRAINING_NOT_FOUND)); return
	 * moduleRepository.findByCIdOrderByNumber(moduleVO.getTrainingId()); }
	 */

	private void checkUserId(String userId) {
		if (StringUtils.isBlank(userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
	}

	private void checkModuleId(String moduleId) {
		if (StringUtils.isBlank(moduleId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_TRAINING_ID);
	}

	

}
