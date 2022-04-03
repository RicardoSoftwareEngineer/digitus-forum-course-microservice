package com.digitusforum.link;

public class LinkVO {
	private String linkId;
	private String moduleVideoId;
	private String userId;
	private String name;
	private String url;
	private int position;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getModuleVideoId() {
		return moduleVideoId;
	}

	public void setModuleVideoId(String moduleVideoId) {
		this.moduleVideoId = moduleVideoId;
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
