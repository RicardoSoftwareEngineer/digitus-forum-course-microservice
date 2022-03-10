package com.digitusforum.course;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.module.ModuleEntity;
import com.digitusforum.module.ModuleVO;

@RestController
public class CourseController {
	@Autowired
	CourseService courseService;

	@RequestMapping(value = "/course/v1/create")
	public CourseEntity create(@RequestBody CourseVO courseVO) {
		return courseService.create(courseVO);
	}

	@RequestMapping(value = "/course/v1/retrieveModulesWithVideosByCourseId")
	public List<ModuleVO> retrieveModulesWithVideosByCourseId(@RequestBody CourseVO courseVO) {
		return courseService.retrieveModulesWithVideosByCourseIdDEPRECATED(courseVO);
	}

	@RequestMapping(value = "/course/v1/retrieveModulesByCourseId")
	public List<ModuleEntity> retrieveModulesByCourseId(@RequestBody CourseVO courseVO) {
		return courseService.retrieveModulesByCourseId(courseVO);
	}

	@RequestMapping(value = "/course/v1/retrieveById")
	public CourseEntity retrieveById(@RequestBody CourseVO courseVO) {
		return courseService.retrieveById(courseVO);
	}
	
	@RequestMapping(value = "/course/v1/retrieveSubjectsByCourseId")
	public CourseVO retrieveSubjectsByCourseId(@RequestBody CourseVO courseVO) {
		return courseService.retrieveSubjectsByCourseId(courseVO);
	}

	@RequestMapping(value = "/course/v1/retrieveAll")
	public List<CourseEntity> retrieveAll() {
		return courseService.retrieveAll();
	}

	@RequestMapping(value = "/course/v1/retrieveByPerfil")
	public List<CourseEntity> retrieveByPerfil(@RequestBody CourseVO courseVO) {
		return courseService.retrieveByPerfil(courseVO);
	}
	
	@RequestMapping(value = "/course/v1/delete")
	public CourseVO delete(@RequestBody CourseVO courseVO) {
		return courseService.delete(courseVO);
	}

	/*
	 * @RequestMapping(value = "/user/v1/{id}/retrieve") public Object
	 * retrieveById(@PathVariable String id) { return trailService.retrieveById(id);
	 * }
	 * 
	 * @RequestMapping(value = "/user/v1/retrieve/byEmailAndPassword") public
	 * TrailVO retrieve(@RequestBody TrailVO user) { return
	 * trailService.retrieveByEmailAndPassword(user); }
	 * 
	 * @RequestMapping(value = "/user/v1/{id}/update") public Object
	 * update(@PathVariable String id, @RequestBody TrailVO user) { return
	 * trailService.update(user, id); }
	 * 
	 * @RequestMapping(value = "/user/v1/{id}/delete") public Object
	 * delete(@PathVariable String id) { return trailService.delete(id); }
	 */
}