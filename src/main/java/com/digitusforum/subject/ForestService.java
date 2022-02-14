package com.digitusforum.subject;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.util.RequestService;

@Service
public class ForestService {

	@Autowired
	SubjectRepository forestRepository;
	RequestService requestService = new RequestService();
	
	public boolean thisForestBelongToThisUser(String forestId, String userId) {
		SubjectEntity forest = forestRepository.findBySubjectIdAndDeletedIsFalse(forestId);
		if (forest == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.FOREST_NOT_FOUND);
		requestService.checkIfThisPerfilBelongsToThisUser(forest.getPerfilId(), userId);
		return true;
	}

	public SubjectEntity create(ForestVO forestVO) {
		if (StringUtils.isBlank(forestVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.FOREST_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(forestVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.FOREST_MISSING_NAME);

		requestService.checkIfThisPerfilBelongsToThisUser(forestVO.getPerfilId(), forestVO.getUserId());

		SubjectEntity forestFromDB = forestRepository.findByPerfilIdAndNameAndDeletedIsFalse(forestVO.getPerfilId(),
				forestVO.getName());
		if (forestFromDB != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.FOREST_NAME_ALREADY_IN_USE);

		SubjectEntity forestEntity = new ModelMapper().map(forestVO, SubjectEntity.class);
		forestEntity = forestRepository.save(forestEntity);
		return forestEntity;
	}

	public List<SubjectEntity> retrieve() {
		return forestRepository.findByDeletedIsFalse();
	}

	public List<SubjectEntity> retrieveByPerfil(ForestVO forestVO) {
		if (StringUtils.isBlank(forestVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.FOREST_MISSING_PERFIL_ID);
		return forestRepository.findByPerfilIdAndDeletedIsFalse(forestVO.getPerfilId());
	}

	public SubjectEntity retrieveById(ForestVO forestVO) {
		if (StringUtils.isBlank(forestVO.getForestId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.FOREST_MISSING_ID);
		
		SubjectEntity forest = forestRepository.findBySubjectIdAndDeletedIsFalse(forestVO.getForestId());
		if(forest == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.FOREST_NOT_FOUND);
			
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
