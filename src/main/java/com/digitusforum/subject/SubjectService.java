package com.digitusforum.subject;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoEntity;
import com.digitusforum.video.VideoRepository;
import com.digitusforum.video.VideoVO;

@Service
public class SubjectService {

	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	VideoRepository videoRepository;
	RequestService requestService = new RequestService();

	public boolean thisSubjectBelongToThisUser(String subjectId, String userId) {
		SubjectEntity subject = subjectRepository.findBySubjectIdAndDeletedIsFalse(subjectId);
		if (subject == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.SUBJECT_NOT_FOUND);
		requestService.checkIfThisPerfilBelongsToThisUser(subject.getPerfilId(), userId);
		return true;
	}

	public SubjectVO create(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_PERFIL_ID);
		if (StringUtils.isBlank(subjectVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_NAME);

		//requestService.checkIfThisPerfilBelongsToThisUser(subjectVO.getPerfilId(), subjectVO.getUserId());

		SubjectEntity subjectFromDB = subjectRepository.findByPerfilIdAndNameAndDeletedIsFalse(subjectVO.getPerfilId(),
				subjectVO.getName());
		if (subjectFromDB != null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_NAME_ALREADY_IN_USE);

		SubjectEntity subjectEntity = new ModelMapper().map(subjectVO, SubjectEntity.class);
		subjectEntity = subjectRepository.save(subjectEntity);
		subjectVO = new ModelMapper().map(subjectEntity, SubjectVO.class);
		return subjectVO;
	}

	public List<SubjectVO> retrieveByPerfilId(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_PERFIL_ID);

		List<SubjectEntity> subjectEntities = subjectRepository
				.findByPerfilIdAndDeletedIsFalse(subjectVO.getPerfilId());
		List<SubjectVO> subjects = new ModelMapper().map(subjectEntities, List.class);
		return subjects;
	}

	public List<SubjectEntity> retrieveByPerfil(SubjectVO forestVO) {
		if (StringUtils.isBlank(forestVO.getPerfilId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_PERFIL_ID);
		return subjectRepository.findByPerfilIdAndDeletedIsFalse(forestVO.getPerfilId());
	}

	public SubjectVO retrieveByIdWithVideos(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_ID);

		SubjectEntity subjectEntity = subjectRepository.findBySubjectIdAndDeletedIsFalse(subjectVO.getSubjectId());
		if (subjectEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.SUBJECT_NOT_FOUND);

		subjectVO = new ModelMapper().map(subjectEntity, SubjectVO.class);
		List<VideoEntity> videoEntities = videoRepository.findBySubjectIdAndDeletedIsFalse(subjectVO.getSubjectId());
		List<VideoVO> videos = new ModelMapper().map(videoEntities, List.class);
		subjectVO.setVideos(videos);
		return subjectVO;
	}
	
	public SubjectVO update(SubjectVO subjectVO) {
		if (StringUtils.isBlank(subjectVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.SUBJECT_MISSING_ID);
		
		SubjectEntity subjectEntity = subjectRepository.findById(subjectVO.getSubjectId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.SUBJECT_NOT_FOUND));
		
		if (StringUtils.isNotBlank(subjectVO.getName()))
			subjectEntity.setName(subjectVO.getName());
		if (StringUtils.isNotBlank(subjectVO.getSinopse()))
			subjectEntity.setSinopse(subjectVO.getSinopse());
		if (StringUtils.isNotBlank(subjectVO.getDescription()))
			subjectEntity.setDescription(subjectVO.getDescription());
		
		subjectRepository.save(subjectEntity);
		subjectVO = new ModelMapper().map(subjectEntity, SubjectVO.class);
		return subjectVO;
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
