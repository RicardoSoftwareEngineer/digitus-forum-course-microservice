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

import com.digitusforum.course.CourseEntity;
import com.digitusforum.course.CourseRepository;
import com.digitusforum.course.CourseService;
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
	CourseRepository courseRepository;
	@Autowired
	LinkRepository linkRepository;
	@Autowired
	CourseService courseService;
	RequestService requestService = new RequestService();

	public ModuleVO create(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);
		if (StringUtils.isBlank(moduleVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_NAME);

		courseService.checkIfThisCourseBelongToThisUser(moduleVO.getCourseId(), moduleVO.getUserId());
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

	public List<ModuleVO> retrieveByCourseId(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);

		List<ModuleEntity> moduleEntities = moduleRepository.findByCourseIdOrderByNumber(moduleVO.getCourseId());
		List<ModuleVO> modules = new ModelMapper().map(moduleEntities, List.class);
		return modules;
	}

	public List<ModuleVO> retrieveByCourseWithVideos(ModuleVO moduleVO) {
		if (StringUtils.isBlank(moduleVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);

		List<ModuleEntity> moduleEntities = moduleRepository.findByCourseIdOrderByNumber(moduleVO.getCourseId());
		List<ModuleVO> modules = new ArrayList<>();
		for (ModuleEntity moduleEntity : moduleEntities)
			modules.add(new ModelMapper().map(moduleEntity, ModuleVO.class));

		List<ModuleVideoEntity> moduleVideoEntities = moduleVideoRepository.findByCourseIdOrderByPositionAsc(moduleVO.getCourseId());
		List<VideoVO> videos = new ArrayList<>();
		for (ModuleVideoEntity moduleVideoEntity : moduleVideoEntities) {
			VideoEntity videoEntity = videoRepository.findById(moduleVideoEntity.getVideoId()).get();
			VideoVO video = new ModelMapper().map(videoEntity, VideoVO.class);
			video.setModuleId(moduleVideoEntity.getModuleId());
			video.setModuleVideoId(moduleVideoEntity.getModuleVideoId());
			List<LinkEntity> linkEntities = linkRepository.findByModuleVideoId(moduleVideoEntity.getModuleVideoId());
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
		if (StringUtils.isBlank(moduleVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);

		courseService.checkIfThisCourseBelongToThisUser(moduleVO.getCourseId(), moduleVO.getUserId());

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
		if (StringUtils.isBlank(moduleVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);
		if (StringUtils.isBlank(moduleVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_ID);
		if (moduleVO.getNewNumber() == 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_NEW_NUMBER);

		checkIfThisModuleBelongToThisUser(moduleVO.getModuleId(), moduleVO.getUserId());
		courseService.checkIfThisCourseBelongToThisUser(moduleVO.getCourseId(), moduleVO.getUserId());

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
		Optional<CourseEntity> courseEntity = courseRepository.findById(moduleEntity.get().getCourseId());
		requestService.checkIfThisPerfilBelongsToThisUser(courseEntity.get().getPerfilId(), userId);
	}

	private void fixNumbers(ModuleVO moduleVO) {
		List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByNumber(moduleVO.getCourseId());
		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).getNumber() != i + 1) {
				modules.get(i).setNumber(i + 1);
				moduleRepository.save(modules.get(i));
			}
		}
	}

	private int getLastModule(ModuleVO moduleVO) {
		List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByNumber(moduleVO.getCourseId());
		if (modules.size() == 0)
			return 1;
		return modules.get(modules.size() - 1).getNumber() + 1;
	}

	/*
	 * public List<ModuleEntity> retrieveByModule(moduleVO moduleVO) {
	 * checkUserId(moduleVO.getUserId()); checkModuleId(moduleVO.getModuleId());
	 * courseRepository.findById(moduleVO.getCourseId()) .orElseThrow(() -> new
	 * ResponseStatusException(HttpStatus.NOT_FOUND, M.COURSE_NOT_FOUND)); return
	 * moduleRepository.findByCIdOrderByNumber(moduleVO.getCourseId()); }
	 */

	private void checkUserId(String userId) {
		if (StringUtils.isBlank(userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
	}

	private void checkModuleId(String moduleId) {
		if (StringUtils.isBlank(moduleId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);
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
