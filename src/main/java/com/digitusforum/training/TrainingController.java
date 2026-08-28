package com.digitusforum.training;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.module.ModuleEntity;
import com.digitusforum.module.ModuleVO;

@RestController
public class TrainingController {
	@Autowired
	TrainingService trainingService;

	@RequestMapping(value = "/training/v1/create")
	public TrainingEntity create(@RequestBody TrainingVO trainingVO) {
		return trainingService.create(trainingVO);
	}

	@RequestMapping(value = "/training/v1/retrieveModulesWithVideosByTrainingId")
	public List<ModuleVO> retrieveModulesWithVideosByTrainingId(@RequestBody TrainingVO trainingVO) {
		return trainingService.retrieveModulesWithVideosByTrainingIdDEPRECATED(trainingVO);
	}

	@RequestMapping(value = "/training/v1/retrieveModulesByTrainingId")
	public List<ModuleEntity> retrieveModulesByTrainingId(@RequestBody TrainingVO trainingVO) {
		return trainingService.retrieveModulesByTrainingId(trainingVO);
	}

	@RequestMapping(value = "/training/v1/retrieveById")
	public TrainingEntity retrieveById(@RequestBody TrainingVO trainingVO) {
		return trainingService.retrieveById(trainingVO);
	}

	@RequestMapping(value = "/training/v1/retrieveSubjectsByTrainingId")
	public TrainingVO retrieveSubjectsByTrainingId(@RequestBody TrainingVO trainingVO) {
		return trainingService.retrieveSubjectsByTrainingId(trainingVO);
	}

	@RequestMapping(value = "/training/v1/retrieveAll")
	public List<TrainingEntity> retrieveAll() {
		return trainingService.retrieveAll();
	}

	@RequestMapping(value = "/training/v1/retrieveByPerfil")
	public List<TrainingEntity> retrieveByPerfil(@RequestBody TrainingVO trainingVO) {
		return trainingService.retrieveByPerfil(trainingVO);
	}

	@RequestMapping(value = "/training/v1/delete")
	public TrainingVO delete(@RequestBody TrainingVO trainingVO) {
		return trainingService.delete(trainingVO);
	}

}