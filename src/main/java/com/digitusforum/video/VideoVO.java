package com.digitusforum.video;

import java.util.List;

import com.digitusforum.link.LinkVO;

public class VideoVO {
	private String videoId;
	private String userId;
	private String subjectId;
	private String subjectName;
	private String moduleVideoId;
	private String moduleId;
	private String name;
	private String url;
	private String previousVideoId;
	private String previousVideoName;
	private String previousVideoSubjectId;
	private String previousVideoModuleId;
	private String nextVideoId;
	private String nextVideoName;
	private String nextVideoSubjectId;
	private String nextVideoModuleId;
	private String sinopse;
	private String description;
	private List<LinkVO> links;
	
	private String thumbnail;
	private boolean deleted;
	
	public List<LinkVO> getLinks() {
		return links;
	}
	public void setLinks(List<LinkVO> links) {
		this.links = links;
	}
	public String getModuleVideoId() {
		return moduleVideoId;
	}
	public void setModuleVideoId(String moduleVideoId) {
		this.moduleVideoId = moduleVideoId;
	}
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPreviousVideoId() {
		return previousVideoId;
	}
	public void setPreviousVideoId(String previousVideoId) {
		this.previousVideoId = previousVideoId;
	}
	public String getPreviousVideoName() {
		return previousVideoName;
	}
	public void setPreviousVideoName(String previousVideoName) {
		this.previousVideoName = previousVideoName;
	}
	public String getPreviousVideoSubjectId() {
		return previousVideoSubjectId;
	}
	public void setPreviousVideoSubjectId(String previousVideoSubjectId) {
		this.previousVideoSubjectId = previousVideoSubjectId;
	}
	public String getPreviousVideoModuleId() {
		return previousVideoModuleId;
	}
	public void setPreviousVideoModuleId(String previousVideoModuleId) {
		this.previousVideoModuleId = previousVideoModuleId;
	}
	public String getNextVideoId() {
		return nextVideoId;
	}
	public void setNextVideoId(String nextVideoId) {
		this.nextVideoId = nextVideoId;
	}
	public String getNextVideoName() {
		return nextVideoName;
	}
	public void setNextVideoName(String nextVideoName) {
		this.nextVideoName = nextVideoName;
	}
	public String getNextVideoSubjectId() {
		return nextVideoSubjectId;
	}
	public void setNextVideoSubjectId(String nextVideoSubjectId) {
		this.nextVideoSubjectId = nextVideoSubjectId;
	}
	public String getNextVideoModuleId() {
		return nextVideoModuleId;
	}
	public void setNextVideoModuleId(String nextVideoModuleId) {
		this.nextVideoModuleId = nextVideoModuleId;
	}
	public String getSinopse() {
		return sinopse;
	}
	public void setSinopse(String sinopse) {
		this.sinopse = sinopse;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


}
