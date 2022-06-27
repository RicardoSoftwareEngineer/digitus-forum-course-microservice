package com.digitusforum.video;

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
import com.digitusforum.link.LinkEntity;
import com.digitusforum.link.LinkRepository;
import com.digitusforum.link.LinkVO;
import com.digitusforum.module.ModuleEntity;
import com.digitusforum.module.ModuleRepository;
import com.digitusforum.module.ModuleVO;
import com.digitusforum.moduleVideo.ModuleVideoEntity;
import com.digitusforum.moduleVideo.ModuleVideoRepository;
import com.digitusforum.subject.SubjectEntity;
import com.digitusforum.subject.SubjectRepository;
import com.digitusforum.subject.SubjectService;
import com.digitusforum.util.RequestService;

@Service
public class VideoService {

	@Autowired
	VideoRepository videoRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	ModuleRepository moduleRepository;
	@Autowired
	ModuleVideoRepository moduleVideoRepository;
	@Autowired
	LinkRepository linkRepository;
	@Autowired
	SubjectService subjectService;
	RequestService requestService = new RequestService();

	public void checkIfThisVideoBelongToThisUser(String videoId, String userId) {
		Optional<VideoEntity> videoEntity = videoRepository.findById(videoId);
		if (videoEntity.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND);
		Optional<SubjectEntity> subjectEntity = subjectRepository.findById(videoEntity.get().getSubjectId());
		requestService.checkIfThisPerfilBelongsToThisUser(subjectEntity.get().getPerfilId(), userId);
	}

	public VideoVO create(VideoVO videoVO) {
		if (StringUtils.isBlank(videoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(videoVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_SUBJECT_ID);
		if (StringUtils.isBlank(videoVO.getName()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_NAME);

		//subjectService.thisSubjectBelongToThisUser(videoVO.getSubjectId(), videoVO.getUserId());

		VideoEntity videoEntity = new ModelMapper().map(videoVO, VideoEntity.class);
		videoEntity = videoRepository.save(videoEntity);
		videoVO = new ModelMapper().map(videoEntity, VideoVO.class);
		return videoVO;
	}

	public VideoVO retrieveById(VideoVO videoVO) {
		if (StringUtils.isBlank(videoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_ID);

		VideoEntity videoEntity = videoRepository.findByVideoIdAndDeletedIsFalse(videoVO.getVideoId());
		if (videoEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND);

		String moduleId = videoVO.getModuleId();
		
		videoVO = new ModelMapper().map(videoEntity, VideoVO.class);

		if (StringUtils.isNotBlank(moduleId)) {
			moduleRepository.findById(moduleId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND));

			videoVO.setModuleId(moduleId);
			videoVO = getPreviousAndNextVideo(videoVO);
			//VideoEntity moduleVideoEntity = videoRepository.findById(videoVO.getVideoId());
			List<LinkEntity> linkEntities = linkRepository.findByVideoId(videoVO.getVideoId());
			List<LinkVO> links = new ModelMapper().map(linkEntities, List.class);
			videoVO.setLinks(links);
		}

		SubjectEntity subject = subjectRepository.findBySubjectIdAndDeletedIsFalse(videoVO.getSubjectId());
		videoVO.setSubjectName(subject.getName());
		return videoVO;
	}

	@Autowired
	CourseRepository courseRepository;

	private VideoVO getPreviousAndNextVideo(VideoVO videoVO) {
		String courseId = moduleRepository.findById(videoVO.getModuleId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.MODULE_NOT_FOUND)).getCourseId();

		List<ModuleEntity> moduleEntities = moduleRepository.findByCourseIdOrderByNumber(courseId);
		List<ModuleVO> modules = new ArrayList<>();
		for (ModuleEntity moduleEntity : moduleEntities)
			modules.add(new ModelMapper().map(moduleEntity, ModuleVO.class));

		List<ModuleVideoEntity> moduleVideoEntities = moduleVideoRepository.findByCourseIdOrderByPositionAsc(courseId);
		List<VideoVO> videos = new ArrayList<>();
		for (ModuleVideoEntity moduleVideoEntity : moduleVideoEntities) {
			VideoEntity videoEntity = videoRepository.findById(moduleVideoEntity.getVideoId()).get();
			VideoVO video = new ModelMapper().map(videoEntity, VideoVO.class);
			video.setModuleId(moduleVideoEntity.getModuleId());
			videos.add(video);
		}

		for (VideoVO video : videos)
			for (ModuleVO module : modules)
				if (video.getModuleId().equals(module.getModuleId()))
					module.getVideos().add(video);

		for (int i = 0; i < modules.size(); i++) {
			for (int j = 0; j < modules.get(i).getVideos().size(); j++) {
				if (modules.get(i).getVideos().get(j).getVideoId().equals(videoVO.getVideoId())) {
					if (j == 0) {
						if(i != 0){
							int lastVideo = modules.get(i -1).getVideos().size() - 1;
							videoVO.setPreviousVideoName(modules.get(i-1).getVideos().get(lastVideo).getName());
							videoVO.setPreviousVideoId(modules.get(i-1).getVideos().get(lastVideo).getVideoId());
							videoVO.setPreviousVideoSubjectId(modules.get(i-1).getVideos().get(lastVideo).getSubjectId());
							videoVO.setPreviousVideoModuleId(modules.get(i-1).getVideos().get(lastVideo).getModuleId());
						}
					}
					if (j != 0) {
						videoVO.setPreviousVideoName(modules.get(i).getVideos().get(j - 1).getName());
						videoVO.setPreviousVideoId(modules.get(i).getVideos().get(j - 1).getVideoId());
						videoVO.setPreviousVideoSubjectId(modules.get(i).getVideos().get(j - 1).getSubjectId());
						videoVO.setPreviousVideoModuleId(modules.get(i).getVideos().get(j - 1).getModuleId());
					}
					if (j != modules.get(i).getVideos().size() - 1) {
						videoVO.setNextVideoName(modules.get(i).getVideos().get(j + 1).getName());
						videoVO.setNextVideoId(modules.get(i).getVideos().get(j + 1).getVideoId());
						videoVO.setNextVideoSubjectId(modules.get(i).getVideos().get(j + 1).getSubjectId());
						videoVO.setNextVideoModuleId(modules.get(i).getVideos().get(j + 1).getModuleId());
					}
					if (j == modules.get(i).getVideos().size() - 1) {
						if(i != modules.size() && modules.get(i+1).getVideos().size() > 0){
							videoVO.setNextVideoName(modules.get(i+1).getVideos().get(0).getName());
							videoVO.setNextVideoId(modules.get(i+1).getVideos().get(0).getVideoId());
							videoVO.setNextVideoSubjectId(modules.get(i+1).getVideos().get(0).getSubjectId());
							videoVO.setNextVideoModuleId(modules.get(i+1).getVideos().get(0).getModuleId());
						}
					}
				}
			}
		}
		return videoVO;

	}

	public List<VideoVO> retrieveBySubject(VideoVO videoVO) {
		if (StringUtils.isBlank(videoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(videoVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_SUBJECT_ID);

		subjectService.thisSubjectBelongToThisUser(videoVO.getSubjectId(), videoVO.getUserId());
		List<VideoEntity> videoEntities = videoRepository.findBySubjectIdAndDeletedIsFalse(videoVO.getSubjectId());
		List<VideoVO> videos = new ModelMapper().map(videoEntities, List.class);
		return videos;
	}

	public VideoVO update(VideoVO videoVO) {
		if (StringUtils.isBlank(videoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_ID);

		VideoEntity videoEntity = videoRepository.findById(videoVO.getVideoId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, M.VIDEO_NOT_FOUND));

		if (StringUtils.isNotBlank(videoVO.getName()))
			videoEntity.setName(videoVO.getName());
		if (StringUtils.isNotBlank(videoVO.getSinopse()))
			videoEntity.setSinopse(videoVO.getSinopse());
		if (StringUtils.isNotBlank(videoVO.getDescription()))
			videoEntity.setDescription(videoVO.getDescription());
		if (StringUtils.isNotBlank(videoVO.getUrl()))
			videoEntity.setUrl(videoVO.getUrl());
		if (StringUtils.isNotBlank(videoVO.getSubjectId()))
			videoEntity.setSubjectId(videoVO.getSubjectId()); //TODO check if this subject exists

		videoRepository.save(videoEntity);
		videoVO = new ModelMapper().map(videoEntity, VideoVO.class);
		return videoVO;
	}

	public VideoVO delete(VideoVO videoVO) {
		if (StringUtils.isBlank(videoVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_USER_ID);
		if (StringUtils.isBlank(videoVO.getSubjectId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_SUBJECT_ID);
		if (StringUtils.isBlank(videoVO.getVideoId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.VIDEO_MISSING_ID);
		if (moduleVideoRepository.findByVideoId(videoVO.getVideoId()).size() > 0)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.REMOVE_THIS_VIDEO_FROM_ALL_MODULES);

		videoRepository.deleteById(videoVO.getVideoId());
		return videoVO;
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
