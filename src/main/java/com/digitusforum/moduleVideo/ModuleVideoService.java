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
import com.digitusforum.module.moduleVO;
import com.digitusforum.subject.SubjectRepository;
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.TreeService;
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
	TreeService treeService;
	RequestService requestService = new RequestService();
	
	public List<VideoEntity> retrieveVideos(moduleVO moduleVO){
		
		
		return null;
	}

	public List<ModuleVideoEntity> reorder(ModuleVideoVO moduleVideoVO) {
		if (StringUtils.isBlank(moduleVideoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(moduleVideoVO.getTreeId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_TREE_ID);
		if (StringUtils.isBlank(moduleVideoVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_KM_ID);
		if (moduleVideoVO.getNewPosition() == 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_NEW_POSITION);

		moduleService.checkIfThisModuleBelongToThisUser(moduleVideoVO.getKmId(), moduleVideoVO.getUserId());
		treeService.checkIfThisTreeBelongToThisUser(moduleVideoVO.getTreeId(), moduleVideoVO.getUserId());

		ModuleVideoEntity kmTreeEntity = moduleVideoRepository.findByModuleIdAndVideoId(moduleVideoVO.getKmId(), moduleVideoVO.getTreeId());
		if (kmTreeEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_NOT_FOUND_IN_KM);

		kmTreeEntity.setPosition(moduleVideoVO.getNewPosition());
		fixPositions(moduleVideoVO);
		return moduleVideoRepository.findByModuleIdOrderByPositionAsc(moduleVideoVO.getKmId());
	}

	private void fixPositions(ModuleVideoVO kmTreeVO) {
		List<ModuleVideoEntity> trees = moduleVideoRepository.findByModuleIdOrderByPositionAsc(kmTreeVO.getKmId());
		for (int i = 1; i <= trees.size(); i++) {
			if (trees.get(i).getPosition() != i) {
				trees.get(i).setPosition(i);
				moduleVideoRepository.save(trees.get(i));
			}
		}
	}

	public ModuleVideoVO removeVideoFromModule(ModuleVideoVO kmTreeVO) {
		if (StringUtils.isBlank(kmTreeVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(kmTreeVO.getTreeId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_TREE_ID);
		if (StringUtils.isBlank(kmTreeVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_KM_ID);

		moduleService.checkIfThisModuleBelongToThisUser(kmTreeVO.getKmId(), kmTreeVO.getUserId());
		treeService.checkIfThisTreeBelongToThisUser(kmTreeVO.getTreeId(), kmTreeVO.getUserId());

		ModuleVideoEntity moduleVideoEntity = moduleVideoRepository.findByModuleIdAndVideoId(kmTreeVO.getKmId(), kmTreeVO.getTreeId());
		if (moduleVideoEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_NOT_FOUND_IN_KM);

		moduleVideoRepository.deleteById(moduleVideoEntity.getModuleVideoId());
		fixPositions(kmTreeVO);
		return kmTreeVO;
	}

	public ModuleVideoEntity addVideoToModule(ModuleVideoVO kmTreeVO) {
		if (StringUtils.isBlank(kmTreeVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(kmTreeVO.getTreeId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_TREE_ID);
		if (StringUtils.isBlank(kmTreeVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_KM_ID);

		moduleService.checkIfThisModuleBelongToThisUser(kmTreeVO.getKmId(), kmTreeVO.getUserId());
		treeService.checkIfThisTreeBelongToThisUser(kmTreeVO.getTreeId(), kmTreeVO.getUserId());

		ModuleVideoEntity kmTreeEntity = moduleVideoRepository.findByModuleIdAndVideoId(kmTreeVO.getKmId(), kmTreeVO.getTreeId());
		if (kmTreeEntity != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_ALREADY_IN_KM);

		kmTreeVO.setPosition(getLastPosition(kmTreeVO));
		kmTreeVO.setUserId(null);
		kmTreeEntity = new ModelMapper().map(kmTreeVO, ModuleVideoEntity.class);
		return moduleVideoRepository.save(kmTreeEntity);
	}

	private int getLastPosition(ModuleVideoVO kmTreeVO) {
		List<ModuleVideoEntity> trees = moduleVideoRepository.findByModuleIdOrderByPositionAsc(kmTreeVO.getKmId());
		if (trees.size() == 0)
			return 1;
		return trees.get(trees.size() - 1).getPosition();
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
