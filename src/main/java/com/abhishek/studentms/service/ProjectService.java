package com.abhishek.studentms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhishek.studentms.exception.EmptyInputException;
import com.abhishek.studentms.exception.ProjectNotFoundException;
import com.abhishek.studentms.model.Project;
import com.abhishek.studentms.repository.ProjectRepository;

@Service
@Transactional(readOnly = true)
public class ProjectService {

	private final ProjectRepository projectRepository;

	public ProjectService(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	public List<Project> getAllProjects() {
		return projectRepository.findAll();
	}

	public Project findProjectById(Long id) {
		if (id == null || id <= 0) {
			throw new EmptyInputException("A valid project id is required.");
		}
		return projectRepository.findById(id)
				.orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " does not exist."));
	}

	@Transactional
	public Project saveProject(Project project) {
		project.setId(null);
		return projectRepository.save(project);
	}

	@Transactional
	public Project updateProject(Long id, Project changes) {
		Project existing = findProjectById(id);
		if (StringUtils.isNotBlank(changes.getProjectName())) {
			existing.setProjectName(changes.getProjectName());
		}
		if (StringUtils.isNotBlank(changes.getProjectDescription())) {
			existing.setProjectDescription(changes.getProjectDescription());
		}
		return projectRepository.save(existing);
	}

	@Transactional
	public void deleteProjectById(Long id) {
		Project project = findProjectById(id);
		projectRepository.delete(project);
	}

}
