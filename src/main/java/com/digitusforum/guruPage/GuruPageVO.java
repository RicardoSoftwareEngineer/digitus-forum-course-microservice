package com.digitusforum.guruPage;

public class GuruPageVO {
	private String guruPageId;
	private String guruId;
	private String titleKey;
	private String src;
	private int position;
	private boolean deleted;

	public String getGuruPageId() {
		return guruPageId;
	}

	public void setGuruPageId(String guruPageId) {
		this.guruPageId = guruPageId;
	}

	public String getGuruId() {
		return guruId;
	}

	public void setGuruId(String guruId) {
		this.guruId = guruId;
	}

	public String getTitleKey() {
		return titleKey;
	}

	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
