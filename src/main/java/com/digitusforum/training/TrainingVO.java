package com.digitusforum.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.digitusforum.module.ModuleVO;
import com.digitusforum.subject.SubjectVO;

public class TrainingVO {
	private String trainingId;
	private String guruId;
	private String userId;
	private String perfilId;
	private String name;
	private String sinopse;
	private String description;
	private boolean paid;
	private Integer price;
	private boolean deleted;
	private List<SubjectVO> subjects = new ArrayList<>();
	private List<ModuleVO> modules = new ArrayList<>();

	

	public List<SubjectVO> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<SubjectVO> subjects) {
		this.subjects = subjects;
	}

	public List<ModuleVO> getModules() {
		return modules;
	}

	public void setModules(List<ModuleVO> modules) {
		this.modules = modules;
	}

	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getGuruId() {
		return guruId;
	}

	public void setGuruId(String guruId) {
		this.guruId = guruId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPerfilId() {
		return perfilId;
	}

	public void setPerfilId(String perfilId) {
		this.perfilId = perfilId;
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

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
