package com.digitusforum.course;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.util.RequestService;

@Service
public class CourseService {

	@Autowired
	CourseRepository courseRepository;
	RequestService requestService = new RequestService();

	public boolean checkIfThisCourseBelongToThisUser(String courseId, String userId) {
		CourseEntity course = courseRepository.findByCourseIdAndDeletedIsFalse(courseId);
		if (course == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.COURSE_NOT_FOUND);
		requestService.checkIfThisPerfilBelongsToThisUser(course.getPerfilId(), userId);
		return true;
	}

	public CourseEntity create(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_USER_ID);
		if (StringUtils.isBlank(courseVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(courseVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_NAME);

		requestService.checkIfThisPerfilBelongsToThisUser(courseVO.getPerfilId(), courseVO.getUserId());

		CourseEntity courseFromDB = courseRepository.findByUserIdAndPerfilIdAndNameAndDeletedIsFalse(courseVO.getUserId(),
				courseVO.getPerfilId(), courseVO.getName());
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
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_COURSE_ID);
		return courseRepository.findByCourseIdAndDeletedIsFalse(courseVO.getCourseId());
	}

	public List<CourseEntity> retrieveAll() {
		return courseRepository.findTop9ByDeletedIsFalse();
	}

	public String retrieveModulesByCourseId(CourseVO courseVO) {
		if (StringUtils.isBlank(courseVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.COURSE_MISSING_USER_ID);
		return null;
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
