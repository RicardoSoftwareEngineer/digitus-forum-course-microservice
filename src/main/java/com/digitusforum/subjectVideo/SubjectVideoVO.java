package com.digitusforum.subjectVideo;

public class SubjectVideoVO {
	private String subjectVideoId;
	private String userId;
	private String subjectId;
	private String videoId;
	private String trainingId;
	private int position;
	private int newPosition;

	public String getSubjectVideoId() {
		return subjectVideoId;
	}

	public void setSubjectVideoId(String subjectVideoId) {
		this.subjectVideoId = subjectVideoId;
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

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
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
