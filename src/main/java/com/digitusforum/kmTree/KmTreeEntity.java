package com.digitusforum.kmTree;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "KmTree")
public class KmTreeEntity {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	private String kmTreeId;
	private String kmId;
	private String treeId;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getKmTreeId() {
		return kmTreeId;
	}

	public void setKmTreeId(String kmTreeId) {
		this.kmTreeId = kmTreeId;
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
