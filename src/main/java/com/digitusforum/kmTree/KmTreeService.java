package com.digitusforum.kmTree;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.forest.ForestRepository;
import com.digitusforum.km.KmRepository;
import com.digitusforum.km.KmService;
import com.digitusforum.trail.TrailRepository;
import com.digitusforum.trail.TrailService;
import com.digitusforum.trailAndCourse.RequestService;
import com.digitusforum.tree.TreeRepository;
import com.digitusforum.tree.TreeService;

@Service
public class KmTreeService {
	//TODO ponderar sobre o serviço ter apenas seu proprio repositorio
	@Autowired
	KmTreeRepository kmTreeRepository;
	@Autowired
	KmRepository kmRepository;
	@Autowired
	TreeRepository treeRepository;
	@Autowired
	TrailRepository trailRepository;
	@Autowired
	ForestRepository forestRepository;
	@Autowired
	TrailService trailService;
	@Autowired
	KmService kmService;
	
	@Autowired
	TreeService treeService;
	RequestService requestService = new RequestService();

	public List<KmTreeEntity> reorder(KmTreeVO kmTreeVO) {
		if (StringUtils.isBlank(kmTreeVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(kmTreeVO.getTreeId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_TREE_ID);
		if (StringUtils.isBlank(kmTreeVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_KM_ID);
		if (kmTreeVO.getNewPosition() == 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_NEW_POSITION);

		kmService.checkIfThisKmBelongToThisUser(kmTreeVO.getKmId(), kmTreeVO.getUserId());
		treeService.checkIfThisTreeBelongToThisUser(kmTreeVO.getTreeId(), kmTreeVO.getUserId());

		KmTreeEntity kmTreeEntity = kmTreeRepository.findByKmIdAndTreeId(kmTreeVO.getKmId(), kmTreeVO.getTreeId());
		if (kmTreeEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_NOT_FOUND_IN_KM);

		kmTreeEntity.setPosition(kmTreeVO.getNewPosition());
		fixPositions(kmTreeVO);
		return kmTreeRepository.findByKmIdOrderByPositionAsc(kmTreeVO.getKmId());
	}

	private void fixPositions(KmTreeVO kmTreeVO) {
		List<KmTreeEntity> trees = kmTreeRepository.findByKmIdOrderByPositionAsc(kmTreeVO.getKmId());
		for (int i = 1; i <= trees.size(); i++) {
			if (trees.get(i).getPosition() != i) {
				trees.get(i).setPosition(i);
				kmTreeRepository.save(trees.get(i));
			}
		}
	}

	public KmTreeVO removeTreeFromKm(KmTreeVO kmTreeVO) {
		if (StringUtils.isBlank(kmTreeVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(kmTreeVO.getTreeId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_TREE_ID);
		if (StringUtils.isBlank(kmTreeVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_KM_ID);

		kmService.checkIfThisKmBelongToThisUser(kmTreeVO.getKmId(), kmTreeVO.getUserId());
		treeService.checkIfThisTreeBelongToThisUser(kmTreeVO.getTreeId(), kmTreeVO.getUserId());

		KmTreeEntity kmTreeEntity = kmTreeRepository.findByKmIdAndTreeId(kmTreeVO.getKmId(), kmTreeVO.getTreeId());
		if (kmTreeEntity == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_NOT_FOUND_IN_KM);

		kmTreeRepository.deleteById(kmTreeEntity.getKmTreeId());
		fixPositions(kmTreeVO);
		return kmTreeVO;
	}

	public KmTreeEntity addTreeToKm(KmTreeVO kmTreeVO) {
		if (StringUtils.isBlank(kmTreeVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_USER_ID);
		if (StringUtils.isBlank(kmTreeVO.getTreeId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_TREE_ID);
		if (StringUtils.isBlank(kmTreeVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_TREE_MISSING_KM_ID);

		kmService.checkIfThisKmBelongToThisUser(kmTreeVO.getKmId(), kmTreeVO.getUserId());
		treeService.checkIfThisTreeBelongToThisUser(kmTreeVO.getTreeId(), kmTreeVO.getUserId());

		KmTreeEntity kmTreeEntity = kmTreeRepository.findByKmIdAndTreeId(kmTreeVO.getKmId(), kmTreeVO.getTreeId());
		if (kmTreeEntity != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TREE_ALREADY_IN_KM);

		kmTreeVO.setPosition(getLastPosition(kmTreeVO));
		kmTreeVO.setUserId(null);
		kmTreeEntity = new ModelMapper().map(kmTreeVO, KmTreeEntity.class);
		return kmTreeRepository.save(kmTreeEntity);
	}

	private int getLastPosition(KmTreeVO kmTreeVO) {
		List<KmTreeEntity> trees = kmTreeRepository.findByKmIdOrderByPositionAsc(kmTreeVO.getKmId());
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
