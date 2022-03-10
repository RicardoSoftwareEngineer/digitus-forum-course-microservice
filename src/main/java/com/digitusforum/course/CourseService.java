package com.digitusforum.course;

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
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoEntity;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.VideoVO;

@Service
public class CourseService {

	@Autowired
	CourseRepository courseRepository;
	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	VideoRepository videoRepository;
	@Autowired
	ModuleVideoRepository moduleVideoRepository;
	RequestService requestService = new RequestService();

	public List<ModuleVO> retrieveModulesWithVideosByCourseIdDEPRECATED(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_COURSE_ID);

		// List<ModuleEntity> modulesEntity =
		// moduleRepository.findByCourseIdOrderByNumber(courseVO.getCourseId());
		List<ModuleVO> modules = moduleRepository.findByCourseIdOrderByNumber(courseVO.getCourseId()).stream()
				.map(module -> new ModelMapper().map(module, ModuleVO.class)).collect(Collectors.toList());
		List<VideoVO> videos = moduleVideoRepository.findByCourseId(courseVO.getCourseId()).stream()
				.map(video -> new ModelMapper().map(video, VideoVO.class)).collect(Collectors.toList());

		for (VideoVO video : videos)
			for (ModuleVO module : modules)
				if (video.getModuleId().equals(module.getModuleId()))
					module.getVideos().add(video);

		return modules;
	}

	public List<ModuleVideoEntity> retrieveVideosByCourseId(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_VIDEO_MISSING_COURSE_ID);

		return moduleVideoRepository.findByCourseId(courseVO.getCourseId());
	}

	public boolean checkIfThisCourseBelongToThisUser(String courseId, String userId) {
		CourseEntity course = courseRepository.findByCourseIdAndDeletedIsFalse(courseId);
		if (course == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.COURSE_NOT_FOUND);
		requestService.checkIfThisPerfilBelongsToThisUser(course.getPerfilId(), userId);
		return true;
	}

	public List<ModuleEntity> retrieveModulesByCourseId(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_ID);

		return moduleRepository.findByCourseIdOrderByNumber(courseVO.getCourseId());
	}

	public CourseEntity create(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_USER_ID);
		if (StringUtils.isBlank(courseVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(courseVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_NAME);

		requestService.checkIfThisPerfilBelongsToThisUser(courseVO.getPerfilId(), courseVO.getUserId());

		CourseEntity courseFromDB = courseRepository.findByUserIdAndPerfilIdAndNameAndDeletedIsFalse(
				courseVO.getUserId(), courseVO.getPerfilId(), courseVO.getName());
		if (courseFromDB != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_NAME_ALREADY_IN_USE);

		CourseEntity courseEntity = new ModelMapper().map(courseVO, CourseEntity.class);
		courseEntity = courseRepository.save(courseEntity);
		return courseEntity;
	}

	public List<CourseEntity> retrieveByPerfil(CourseVO trailVO) {
		if (StringUtils.isBlank(trailVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_USER_ID);
		requestService.checkIfThisPerfilBelongsToThisUser(trailVO.getPerfilId(), trailVO.getUserId());
		return courseRepository.findByPerfilIdAndDeletedIsFalse(trailVO.getPerfilId());
	}

	public List<CourseEntity> retrieveByForest(CourseVO trailVO) {
		if (StringUtils.isBlank(trailVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_USER_ID);
		return null;
	}

	public CourseEntity retrieveById(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_USER_ID);
		if (StringUtils.isBlank(courseVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_ID);
		return courseRepository.findByCourseIdAndDeletedIsFalse(courseVO.getCourseId());
	}

	public List<CourseEntity> retrieveAll() {
		return courseRepository.findTop9ByDeletedIsFalse();
	}

	public CourseVO delete(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_USER_ID);
		if (StringUtils.isBlank(courseVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(courseVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_ID);

		requestService.checkIfThisPerfilBelongsToThisUser(courseVO.getPerfilId(), courseVO.getUserId());

		CourseEntity courseFromDB = courseRepository.findByCourseIdAndDeletedIsFalse(courseVO.getCourseId());
		if (courseFromDB == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_NOT_FOUND);

		courseRepository.deleteById(courseVO.getCourseId());
		return courseVO;
	}
	
	public CourseVO retrieveSubjectsByCourseId(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_ID);

		List<ModuleVideoEntity> moduleVideoEntities = moduleVideoRepository.findByCourseId(courseVO.getCourseId());
		List<VideoVO> videos = new ArrayList<>();
		for (ModuleVideoEntity moduleVideoEntity : moduleVideoEntities) {
			
			//VideoEntity videoEntity = videoRepository.findById(moduleVideoEntity.getVideoId()).get();
			VideoEntity videoEntity = videoRepository.findById(moduleVideoEntity.getVideoId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND));
			SubjectEntity subjectEntitiy = subjectRepository.findBySubjectIdAndDeletedIsFalse(videoEntity.getSubjectId());
			if(subjectEntitiy == null)
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_NOT_FOUND);
			SubjectVO subject = new ModelMapper().map(subjectEntitiy, SubjectVO.class);
			if(!courseVO.getSubjects().contains(subject))
				courseVO.getSubjects().add(subject);
		}
		return courseVO;
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
