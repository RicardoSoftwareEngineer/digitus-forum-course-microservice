package com.digitusforum.moduleVideo;

public class ModuleVideoVO {
	private String moduleVideoId;
	private String userId;
	private String moduleId;
	private String videoId;
	private String courseId;
	private int position;
	private int newPosition;

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getModuleVideoId() {
		return moduleVideoId;
	}

	public void setModuleVideoId(String moduleVideoId) {
		this.moduleVideoId = moduleVideoId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getNewPosition() {
		return newPosition;
	}

	public void setNewPosition(int newPosition) {
		this.newPosition = newPosition;
	}

}
