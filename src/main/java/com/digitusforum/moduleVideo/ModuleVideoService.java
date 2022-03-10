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
		if (moduleVideoVO.getNewPosition() == 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_NEW_POSITION);

		moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getModuleId(), moduleVideoVO.getUserId());
		videoService.checkIfThisVideoBelongToThisUser(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());

		ModuleVideoEntity moduleVideoEntity = moduleVideoRepository.findByModuleIdAndVideoId(moduleVideoVO.getModuleId(), moduleVideoVO.getVideoId());
		if (moduleVideoEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_NOT_FOUND_IN_MODULE);

		moduleVideoEntity.setPosition(moduleVideoVO.getNewPosition());
		fixPositions(moduleVideoVO);
		return moduleVideoRepository.findByModuleIdOrderByPositionAsc(moduleVideoVO.getModuleId());
	}

	private void fixPositions(ModuleVideoVO moduleVideoVO) {
		List<ModuleVideoEntity> videos = moduleVideoRepository.findByModuleIdOrderByPositionAsc(moduleVideoVO.getModuleId());
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

		moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getModuleId(), moduleVideoVO.getUserId());
		videoService.checkIfThisVideoBelongToThisUser(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());

		ModuleVideoEntity moduleVideoEntity = moduleVideoRepository.findByModuleIdAndVideoId(moduleVideoVO.getModuleId(), moduleVideoVO.getVideoId());
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

		moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getModuleId(), moduleVideoVO.getUserId());
		videoService.checkIfThisVideoBelongToThisUser(moduleVideoVO.getVideoId(), moduleVideoVO.getUserId());
		//TODO $$$ checkIfThisModuleBelongsToThisCourse

		ModuleVideoEntity moduleVideoEntity = moduleVideoRepository.findByModuleIdAndVideoId(moduleVideoVO.getModuleId(), moduleVideoVO.getVideoId());
		if (moduleVideoEntity != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_ALREADY_IN_MODULE);

		moduleVideoVO.setPosition(getLastPosition(moduleVideoVO));
		moduleVideoVO.setUserId(null);
		moduleVideoEntity = new ModelMapper().map(moduleVideoVO, ModuleVideoEntity.class);
		moduleVideoEntity = moduleVideoRepository.save(moduleVideoEntity);
		moduleVideoVO = new ModelMapper().map(moduleVideoEntity, ModuleVideoVO.class);
		return moduleVideoVO;
	}

	private int getLastPosition(ModuleVideoVO moduleVideoVO) {
		List<ModuleVideoEntity> videos = moduleVideoRepository.findByModuleIdOrderByPositionAsc(moduleVideoVO.getModuleId());
		if (videos.size() == 0)
			return 1;
		return videos.get(videos.size() - 1).getPosition();
	}

	/*
	 * public TrailEntity retrieveById(String id) { Optional<TrailEntity> user =
	 * trailRepository.findById(id); if (user.isEmpty()) throw
	 * ThrowService.doIt(404, M.USER_NOT_FOUND); return user.get(); }
	 * 
	 * public TrailVO retrieveByEmailAndPassword(TrailVO userVO) { if
	 * (StringUtils.isBlank(userVO.getEmail())) throw new
	 * ResponseStatusException(HttpStatus.FORBIDDEN, M.LOGIN_MISSING_EMAIL); if
	 * (StringUtils.isBlank(userVO.getPassword())) throw new
	 * ResponseStatusException(HttpStatus.FORBIDDEN, M.LOGIN_MISSING_PASSWORD);
	 * 
	 * Optional<TrailEntity> userFromDB =
	 * trailRepository.findByEmailAndPasswordAndDeletedIsFalse(userVO.getEmail(),
	 * userVO.getPassword()); if (!userFromDB.isPresent()) throw new
	 * ResponseStatusException(HttpStatus.NOT_FOUND,
	 * M.LOGIN_WRONG_LOGIN_OR_PASSWORD);
	 * 
	 * userVO.setId(userFromDB.get().getId().toString()); userVO.setPassword(null);
	 * 
	 * return userVO; }
	 * 
	 * public TrailVO update(TrailVO user, String id) { if
	 * (StringUtils.isBlank(user.getEmail())) throw new
	 * ResponseStatusException(HttpStatus.FORBIDDEN, M.LOGIN_MISSING_EMAIL); if
	 * (StringUtils.isBlank(user.getPassword())) throw new
	 * ResponseStatusException(HttpStatus.FORBIDDEN, M.LOGIN_MISSING_PASSWORD);
	 * 
	 * Optional<TrailEntity> userFromDB = trailRepository.findById(id); if
	 * (userFromDB.isEmpty()) throw new
	 * ResponseStatusException(HttpStatus.NOT_FOUND, M.USER_NOT_FOUND);
	 * 
	 * userFromDB =
	 * trailRepository.findByEmailAndIdNotAndDeletedIsFalse(user.getEmail(), id); if
	 * (userFromDB.isPresent()) throw new
	 * ResponseStatusException(HttpStatus.FORBIDDEN, M.USER_EMAIL_ALREADY_IN_USE);
	 * 
	 * user.setId(id); trailRepository.save(new TrailEntity(user)); return user; }
	 * 
	 * public TrailEntity delete(String id) { Optional<TrailEntity> userFromDB =
	 * trailRepository.findById(id); if (userFromDB.isEmpty()) throw new
	 * ResponseStatusException(HttpStatus.NOT_FOUND, M.USER_NOT_FOUND);
	 * 
	 * TrailEntity user = userFromDB.get(); user.setDeleted(true);
	 * trailRepository.save(user); user.setPassword(""); return user; }
	 * 
	 * public void deleteTest(String locale, String id) { Optional<TrailEntity>
	 * userFromDB = trailRepository.findById(id); if (userFromDB.isEmpty()) throw
	 * ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
	 * 
	 * trailRepository.delete(userFromDB.get()); }
	 */

}
