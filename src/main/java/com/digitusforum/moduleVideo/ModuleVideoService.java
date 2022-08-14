package com.digitusforum.moduleVideo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.course.CourseRepository;
import com.digitusforum.course.CourseService;
import com.digitusforum.module.ModuleRepository;
import com.digitusforum.module.ModuleService;
import com.digitusforum.module.ModuleVO;
import com.digitusforum.subject.SubjectRepository;
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.VideoService;
import com.digitusforum.video.VideoEntity;

@Service
public class ModuleVideoService {
	//TODO ponderar sobre o serviço ter apenas seu proprio repositorio
	@Autowired
	ModuleVideoRepository moduleVideoRepository;
	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	VideoRepository videoRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	CourseService courseService;
	@Autowired
	ModuleService moduleService;
	@Autowired
	VideoService videoService;
	RequestService requestService = new RequestService();
	
	public List<ModuleVO> retrieveModulesWithVideos(ModuleVO moduleVO){
		
		
		return null;
	}

	public List<ModuleVideoEntity> reorder(ModuleVideoVO moduleVideoVO) {
		if (StringUtils.isBlank(moduleVideoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVideoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_VIDEO_ID);
		if (StringUtils.isBlank(moduleVideoVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_MODULE_ID);

		ModuleVideoEntity moduleVideoEntity = moduleVideoRepository.findByModuleIdAndVideoIdAndUserId(moduleVideoVO.getModuleId(), moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());
		if (moduleVideoEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_NOT_FOUND_IN_MODULE);
		
		ModuleVideoEntity moduleVideoEntity2 = moduleVideoRepository.findByModuleIdAndPositionAndUserId(moduleVideoVO.getModuleId(), moduleVideoVO.getNewPosition(), moduleVideoVO.getUserId());
		if(moduleVideoEntity2 != null) {
			addToTheTop(moduleVideoEntity2);
		}
		moduleVideoEntity.setPosition(moduleVideoVO.getNewPosition());
		moduleVideoRepository.save(moduleVideoEntity);
		fixPositions(moduleVideoVO);
		return moduleVideoRepository.findByUserIdAndModuleIdOrderByPositionAsc(moduleVideoVO.getUserId(), moduleVideoVO.getModuleId());
	}

	private void addToTheTop(ModuleVideoEntity moduleVideoEntity2) {
		List<ModuleVideoEntity> videos = moduleVideoRepository.findByModuleIdAndPositionGreaterThanEqualOrderByPositionAsc(moduleVideoEntity2.getModuleId(), moduleVideoEntity2.getPosition());
		for (int i = 0; i < videos.size(); i++) {
			videos.get(i).setPosition(videos.get(i).getPosition() + 1);
			moduleVideoRepository.save(videos.get(i));
		}
	}

	private void fixPositions(ModuleVideoVO moduleVideoVO) {
		List<ModuleVideoEntity> videos = moduleVideoRepository.findByUserIdAndModuleIdOrderByPositionAsc(moduleVideoVO.getUserId(), moduleVideoVO.getModuleId());
		for (int i = 0; i < videos.size(); i++) {
			if (videos.get(i).getPosition() != i) {
				videos.get(i).setPosition(i);
				moduleVideoRepository.save(videos.get(i));
			}
		}
	}

	public ModuleVideoVO removeVideoFromModule(ModuleVideoVO moduleVideoVO) {
		if (StringUtils.isBlank(moduleVideoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVideoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_VIDEO_ID);
		if (StringUtils.isBlank(moduleVideoVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_MODULE_ID);

		//moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getModuleId(), moduleVideoVO.getUserId());
		//videoService.checkIfThisVideoBelongToThisUser(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());

		ModuleVideoEntity moduleVideoEntity = moduleVideoRepository.findByModuleIdAndVideoIdAndUserId(moduleVideoVO.getModuleId(), moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());
		if (moduleVideoEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_NOT_FOUND_IN_MODULE);

		moduleVideoRepository.deleteById(moduleVideoEntity.getModuleVideoId());
		fixPositions(moduleVideoVO);
		return moduleVideoVO;
	}

	public ModuleVideoVO addVideoToModule(ModuleVideoVO moduleVideoVO) {
		if (StringUtils.isBlank(moduleVideoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVideoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_VIDEO_ID);
		if (StringUtils.isBlank(moduleVideoVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_MODULE_ID);
		if (StringUtils.isBlank(moduleVideoVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_COURSE_ID);

		//moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getModuleId(), moduleVideoVO.getUserId());
		//videoService.checkIfThisVideoBelongToThisUser(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());
		//TODO $$$ checkIfThisModuleBelongsToThisCourse

		videoRepository.findByUserIdAndVideoIdAndDeletedIsFalse(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND));
		
		moduleRepository.findByModuleIdAndCourseId(moduleVideoVO.getModuleId(), moduleVideoVO.getCourseId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND));
		
		ModuleVideoEntity moduleVideoEntity = moduleVideoRepository.findByModuleIdAndVideoIdAndUserId(moduleVideoVO.getModuleId(), moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());
		if (moduleVideoEntity != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_ALREADY_IN_MODULE);

		moduleVideoVO.setPosition(getLastPosition(moduleVideoVO) + 1);
		moduleVideoEntity = new ModelMapper().map(moduleVideoVO, ModuleVideoEntity.class);
		moduleVideoEntity = moduleVideoRepository.save(moduleVideoEntity);
		moduleVideoVO = new ModelMapper().map(moduleVideoEntity, ModuleVideoVO.class);
		return moduleVideoVO;
	}

	private int getLastPosition(ModuleVideoVO moduleVideoVO) {
		List<ModuleVideoEntity> videos = moduleVideoRepository.findByUserIdAndModuleIdOrderByPositionAsc(moduleVideoVO.getUserId(), moduleVideoVO.getModuleId());
		if (videos.size() == 0)
			return 1;
		return videos.get(videos.size() - 1).getPosition();
	}

	

}
