package com.digitusforum.trail;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.trailAndCourse.RequestService;

@Service
public class TrailService {

	@Autowired
	TrailRepository trailRepository;
	RequestService requestService = new RequestService();

	public boolean checkIfThisTrailBelongToThisUser(String trailId, String userId) {
		TrailEntity trail = trailRepository.findByTrailIdAndDeletedIsFalse(trailId);
		if (trail == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.TRAIL_NOT_FOUND);
		requestService.checkIfThisPerfilBelongsToThisUser(trail.getPerfilId(), userId);
		return true;
	}

	public TrailEntity create(TrailVO trailVO) {
		if (StringUtils.isBlank(trailVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_MISSING_USER_ID);
		if (StringUtils.isBlank(trailVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(trailVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_MISSING_NAME);

		requestService.checkIfThisPerfilBelongsToThisUser(trailVO.getPerfilId(), trailVO.getUserId());

		TrailEntity trailFromDB = trailRepository.findByUserIdAndPerfilIdAndNameAndDeletedIsFalse(trailVO.getUserId(),
				trailVO.getPerfilId(), trailVO.getName());
		if (trailFromDB != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_NAME_ALREADY_IN_USE);

		TrailEntity trailEntity = new ModelMapper().map(trailVO, TrailEntity.class);
		trailEntity = trailRepository.save(trailEntity);
		return trailEntity;
	}

	public List<TrailEntity> retrieveByPerfil(TrailVO trailVO) {
		if (StringUtils.isBlank(trailVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_MISSING_USER_ID);
		requestService.checkIfThisPerfilBelongsToThisUser(trailVO.getPerfilId(), trailVO.getUserId());
		return trailRepository.findByPerfilIdAndDeletedIsFalse(trailVO.getPerfilId());
	}

	public List<TrailEntity> retrieveByForest(TrailVO trailVO) {
		if (StringUtils.isBlank(trailVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_MISSING_USER_ID);
		return null;
	}

	public TrailEntity retrieveById(TrailVO trailVO) {
		if (StringUtils.isBlank(trailVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_MISSING_USER_ID);
		if (StringUtils.isBlank(trailVO.getTrailId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.TRAIL_MISSING_TRAIL_ID);
		requestService.checkIfThisPerfilBelongsToThisUser(trailVO.getPerfilId(), trailVO.getUserId());
		return trailRepository.findByTrailIdAndDeletedIsFalse(trailVO.getTrailId());
	}

	public List<TrailEntity> retrieve() {
		return trailRepository.findByDeletedIsFalse();
	}

	public String retrieveKms(TrailVO trailVO) {
		// TODO Auto-generated method stub
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
