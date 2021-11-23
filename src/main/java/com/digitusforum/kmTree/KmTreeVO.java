package com.digitusforum.kmTree;

public class KmTreeVO {
	private String kmTreeId;
	private String userId;
	private String kmId;
	private String treeId;
	private int position;
	private int newPosition;

	public String getKmTreeId() {
		return kmTreeId;
	}

	public void setKmTreeId(String kmTreeId) {
		this.kmTreeId = kmTreeId;
	}

	public int getNewPosition() {
		return newPosition;
	}

	public void setNewPosition(int newPosition) {
		this.newPosition = newPosition;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getKmId() {
		return kmId;
	}

	public void setKmId(String kmId) {
		this.kmId = kmId;
	}

	public String getTreeId() {
		return treeId;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

}
