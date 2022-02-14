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
import com.digitusforum.moduleVideo.ModuleVideoEntity;
import com.digitusforum.moduleVideo.ModuleVideoRepository;
import com.digitusforum.util.RequestService;

@Service
public class ModuleService {

	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	ModuleVideoRepository kmTreeRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	CourseService courseService;
	RequestService requestService = new RequestService();

	public List<ModuleEntity> reorder(moduleVO moduleVO) {
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

		Optional<ModuleEntity> kmEntity = moduleRepository.findById(moduleVO.getModuleId());
		if (kmEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_NOT_FOUND);
		kmEntity.get().setNumber(moduleVO.getNewNumber());
		moduleRepository.save(kmEntity.get());
		fixNumbers(moduleVO);
		List<ModuleEntity> kms = new ArrayList<>(); //retrieveByModule(moduleVO);
		return kms;
	}

	public void checkIfThisModuleBelongToThisUser(String moduleId, String userId) {
		Optional<ModuleEntity> moduleEntity = moduleRepository.findById(moduleId);
		if (moduleEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND);
		Optional<CourseEntity> courseEntity = courseRepository.findById(moduleEntity.get().getCourseId());
		requestService.checkIfThisPerfilBelongsToThisUser(courseEntity.get().getPerfilId(), userId);
	}

	public moduleVO delete(moduleVO kmVO) {
		if (StringUtils.isBlank(kmVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
		if (StringUtils.isBlank(kmVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_ID);
		if (StringUtils.isBlank(kmVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);

		courseService.checkIfThisCourseBelongToThisUser(kmVO.getCourseId(), kmVO.getUserId());

		moduleRepository.findById(kmVO.getModuleId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND));

		List<ModuleVideoEntity> kmTrees = kmTreeRepository.findByModuleIdOrderByPositionAsc(kmVO.getModuleId());
		for (ModuleVideoEntity tree : kmTrees)
			kmTreeRepository.deleteById(tree.getModuleId());

		moduleRepository.deleteById(kmVO.getModuleId());
		fixNumbers(kmVO);
		return kmVO;
	}

	private void fixNumbers(moduleVO moduleVO) {
		List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByNumber(moduleVO.getCourseId());
		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).getNumber() != i + 1) {
				modules.get(i).setNumber(i + 1);
				moduleRepository.save(modules.get(i));
			}
		}
	}

	private int getLastModule(moduleVO moduleVO) {
		List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByNumber(moduleVO.getCourseId());
		if (modules.size() == 0)
			return 1;
		return modules.get(modules.size() - 1).getNumber() + 1;
	}

	public ModuleEntity create(moduleVO kmVO) {
		if (StringUtils.isBlank(kmVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
		if (StringUtils.isBlank(kmVO.getCourseId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_COURSE_ID);
		if (StringUtils.isBlank(kmVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_NAME);

		courseService.checkIfThisCourseBelongToThisUser(kmVO.getCourseId(), kmVO.getUserId());

		kmVO.setNumber(getLastModule(kmVO));
		ModuleEntity kmEntity = new ModelMapper().map(kmVO, ModuleEntity.class);
		kmEntity = moduleRepository.save(kmEntity);
		return kmEntity;
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

	public ModuleEntity retrieveById(moduleVO kmVO) {
		if (StringUtils.isBlank(kmVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.MODULE_MISSING_USER_ID);
		return moduleRepository.findById(kmVO.getModuleId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND));
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
