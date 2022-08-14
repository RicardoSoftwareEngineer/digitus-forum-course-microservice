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

		videoRepository.findByUserIdAndVideoIdAndDeletedIsFalse(linkVO.getVideoId(), linkVO.getUserId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND));

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

	

}
