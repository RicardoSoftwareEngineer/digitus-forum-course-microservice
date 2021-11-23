package com.digitusforum.km;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.kmTree.KmTreeEntity;
import com.digitusforum.kmTree.KmTreeRepository;
import com.digitusforum.trail.TrailEntity;
import com.digitusforum.trail.TrailRepository;
import com.digitusforum.trail.TrailService;
import com.digitusforum.trailAndCourse.RequestService;

@Service
public class KmService {

	@Autowired
	KmRepository kmRepository;
	@Autowired
	KmTreeRepository kmTreeRepository;
	@Autowired
	TrailRepository trailRepository;
	@Autowired
	TrailService trailService;
	RequestService requestService = new RequestService();

	public List<KmEntity> reorder(KmVO kmVO) {
		if (StringUtils.isBlank(kmVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_USER_ID);
		if (StringUtils.isBlank(kmVO.getTrailId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_TRAIL_ID);
		if (StringUtils.isBlank(kmVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_ID);
		if (kmVO.getNewNumber() == 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_NEW_NUMBER);

		checkIfThisKmBelongToThisUser(kmVO.getKmId(), kmVO.getUserId());
		trailService.checkIfThisTrailBelongToThisUser(kmVO.getTrailId(), kmVO.getUserId());

		Optional<KmEntity> kmEntity = kmRepository.findById(kmVO.getKmId());
		if (kmEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_NOT_FOUND);
		kmEntity.get().setNumber(kmVO.getNewNumber());
		kmRepository.save(kmEntity.get());
		fixNumbers(kmVO);
		List<KmEntity> kms = retrieveByTrail(kmVO);
		return kms;
	}

	public void checkIfThisKmBelongToThisUser(String kmId, String userId) {
		Optional<KmEntity> kmEntity = kmRepository.findById(kmId);
		if (kmEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.KM_NOT_FOUND);
		Optional<TrailEntity> trailEntity = trailRepository.findById(kmEntity.get().getTrailId());
		requestService.checkIfThisPerfilBelongsToThisUser(trailEntity.get().getPerfilId(), userId);
	}

	public KmVO delete(KmVO kmVO) {
		if (StringUtils.isBlank(kmVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_USER_ID);
		if (StringUtils.isBlank(kmVO.getKmId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_ID);
		if (StringUtils.isBlank(kmVO.getTrailId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_TRAIL_ID);

		trailService.checkIfThisTrailBelongToThisUser(kmVO.getTrailId(), kmVO.getUserId());

		kmRepository.findById(kmVO.getKmId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.KM_NOT_FOUND));

		List<KmTreeEntity> kmTrees = kmTreeRepository.findByKmIdOrderByPositionAsc(kmVO.getKmId());
		for (KmTreeEntity tree : kmTrees)
			kmTreeRepository.deleteById(tree.getKmId());

		kmRepository.deleteById(kmVO.getKmId());
		fixNumbers(kmVO);
		return kmVO;
	}

	private void fixNumbers(KmVO kmVO) {
		List<KmEntity> kms = kmRepository.findByTrailIdOrderByNumber(kmVO.getTrailId());
		for (int i = 0; i < kms.size(); i++) {
			if (kms.get(i).getNumber() != i + 1) {
				kms.get(i).setNumber(i + 1);
				kmRepository.save(kms.get(i));
			}
		}
	}

	private int getLastKm(KmVO kmVO) {
		List<KmEntity> kms = kmRepository.findByTrailIdOrderByNumber(kmVO.getTrailId());
		if (kms.size() == 0)
			return 1;
		return kms.get(kms.size() - 1).getNumber() + 1;
	}

	public KmEntity create(KmVO kmVO) {
		if (StringUtils.isBlank(kmVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_USER_ID);
		if (StringUtils.isBlank(kmVO.getTrailId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_TRAIL_ID);
		if (StringUtils.isBlank(kmVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_NAME);

		trailService.checkIfThisTrailBelongToThisUser(kmVO.getTrailId(), kmVO.getUserId());

		kmVO.setNumber(getLastKm(kmVO));
		KmEntity kmEntity = new ModelMapper().map(kmVO, KmEntity.class);
		kmEntity = kmRepository.save(kmEntity);
		return kmEntity;
	}

	public List<KmEntity> retrieveByTrail(KmVO kmVO) {
		checkUserId(kmVO.getUserId());
		checkTrailId(kmVO.getTrailId());
		trailRepository.findById(kmVO.getTrailId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.TRAIL_NOT_FOUND));
		return kmRepository.findByTrailIdOrderByNumber(kmVO.getTrailId());
	}

	private void checkUserId(String userId) {
		if (StringUtils.isBlank(userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_USER_ID);
	}

	private void checkTrailId(String trailId) {
		if (StringUtils.isBlank(trailId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_TRAIL_ID);
	}

	public KmEntity retrieveById(KmVO kmVO) {
		if (StringUtils.isBlank(kmVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.KM_MISSING_USER_ID);
		return kmRepository.findById(kmVO.getKmId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.KM_NOT_FOUND));
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
