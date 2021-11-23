package com.digitusforum.tree;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.forest.ForestEntity;
import com.digitusforum.forest.ForestRepository;
import com.digitusforum.forest.ForestService;
import com.digitusforum.trailAndCourse.RequestService;

@Service
public class TreeService {

	@Autowired
	TreeRepository treeRepository;
	@Autowired
	ForestRepository forestRepository;
	@Autowired
	ForestService forestService;
	RequestService requestService = new RequestService();

	public void checkIfThisTreeBelongToThisUser(String treeId, String userId) {
		Optional<TreeEntity> treeEntity = treeRepository.findById(treeId);
		if (treeEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.TREE_NOT_FOUND);
		Optional<ForestEntity> forestEntity = forestRepository.findById(treeEntity.get().getForestId());
		requestService.checkIfThisPerfilBelongsToThisUser(forestEntity.get().getPerfilId(), userId);
	}

	public TreeEntity create(TreeVO treeVO) {
		if (StringUtils.isBlank(treeVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(treeVO.getForestId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_MISSING_FOREST_ID);
		if (StringUtils.isBlank(treeVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_MISSING_NAME);

		forestService.thisForestBelongToThisUser(treeVO.getForestId(), treeVO.getUserId());

		TreeEntity treeEntity = new ModelMapper().map(treeVO, TreeEntity.class);
		treeEntity = treeRepository.save(treeEntity);
		return treeEntity;
	}

	public List<TreeEntity> retrieve() {
		return treeRepository.findByDeletedIsFalse();
	}

	public List<TreeEntity> retrieveByForest(TreeVO treeVO) {
		if (StringUtils.isBlank(treeVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(treeVO.getForestId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_MISSING_FOREST_ID);

		forestService.thisForestBelongToThisUser(treeVO.getForestId(), treeVO.getUserId());

		return treeRepository.findByForestIdAndDeletedIsFalse(treeVO.getForestId());
	}

	public TreeEntity retrieveById(TreeVO treeVO) {
		if (StringUtils.isBlank(treeVO.getTreeId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_MISSING_ID);

		TreeEntity forest = treeRepository.findByTreeIdAndDeletedIsFalse(treeVO.getTreeId());
		if (forest == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.TREE_NOT_FOUND);

		return forest;
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
