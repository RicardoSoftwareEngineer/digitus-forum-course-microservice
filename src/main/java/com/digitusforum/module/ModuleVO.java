package com.digitusforum.module;

import java.util.ArrayList;
import java.util.List;

import com.digitusforum.moduleVideo.ModuleVideoVO;
import com.digitusforum.subject.SubjectVO;
import com.digitusforum.video.VideoVO;

public class ModuleVO {
	private String moduleId;
	private String userId;
	private String courseId;
	private String videoId;
	private int number;
	private int newNumber;
	private String name;
	private String sinopse;
	private String description;
	private List<VideoVO> videos = new ArrayList<>();
	private List<SubjectVO> subjects = new ArrayList<>();

	public List<SubjectVO> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<SubjectVO> subjects) {
		this.subjects = subjects;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public List<VideoVO> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoVO> videos) {
		this.videos = videos;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNewNumber() {
		return newNumber;
	}

	public void setNewNumber(int newNumber) {
		this.newNumber = newNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
