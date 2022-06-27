package com.digitusforum.link;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.course.CourseRepository;
import com.digitusforum.module.ModuleEntity;
import com.digitusforum.module.ModuleRepository;
import com.digitusforum.module.ModuleVO;
import com.digitusforum.moduleVideo.ModuleVideoEntity;
import com.digitusforum.moduleVideo.ModuleVideoRepository;
import com.digitusforum.subject.SubjectEntity;
import com.digitusforum.subject.SubjectRepository;
import com.digitusforum.subject.SubjectService;
import com.digitusforum.util.RequestService;
import com.digitusforum.video.VideoRepository;

@Service
public class LinkService {

	@Autowired
	LinkRepository linkRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	ModuleVideoRepository moduleVideoRepository;
	@Autowired
	VideoRepository videoRepository;
	@Autowired
	SubjectService subjectService;
	RequestService requestService = new RequestService();

	public void checkIfThisLinkBelongToThisUser(String linkId, String userId) {

	}

	public LinkVO create(LinkVO linkVO) {
		if (StringUtils.isBlank(linkVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.LINK_MISSING_VIDEO_ID);
		if (StringUtils.isBlank(linkVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.LINK_MISSING_NAME);
		if (StringUtils.isBlank(linkVO.getUrl()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.LINK_MISSING_URL);

		videoRepository.findById(linkVO.getVideoId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND));

		// subjectService.thisSubjectBelongToThisUser(linkVO.getLinkId(),
		// linkVO.getUserId());

		LinkEntity linkEntity = new ModelMapper().map(linkVO, LinkEntity.class);
		linkEntity = linkRepository.save(linkEntity);
		linkVO = new ModelMapper().map(linkEntity, LinkVO.class);
		return linkVO;
	}

	public List<LinkVO> retrieveByVideoId(LinkVO linkVO) {
		if (StringUtils.isBlank(linkVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.LINK_MISSING_VIDEO_ID);

		videoRepository.findById(linkVO.getVideoId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND));

		List<LinkEntity> linkEntities = linkRepository.findByVideoId(linkVO.getVideoId());
		List<LinkVO> links = new ModelMapper().map(linkEntities, List.class);
		return links;
	}

	public LinkVO update(LinkVO linkVO) {
		if (StringUtils.isBlank(linkVO.getLinkId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.LINK_MISSING_ID);

		LinkEntity linkEntity = linkRepository.findById(linkVO.getLinkId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.LINK_NOT_FOUND));

		if (StringUtils.isNotBlank(linkVO.getName()))
			linkEntity.setName(linkVO.getName());
		if (StringUtils.isNotBlank(linkVO.getUrl()))
			linkEntity.setUrl(linkVO.getUrl());

		linkRepository.save(linkEntity);
		linkVO = new ModelMapper().map(linkEntity, LinkVO.class);
		return linkVO;
	}

	public LinkVO delete(LinkVO linkVO) {
		if (StringUtils.isBlank(linkVO.getLinkId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.LINK_MISSING_ID);
		linkRepository.findById(linkVO.getLinkId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.LINK_NOT_FOUND));

		linkRepository.deleteById(linkVO.getLinkId());
		return linkVO;
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
