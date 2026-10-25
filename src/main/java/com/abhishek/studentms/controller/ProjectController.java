package com.abhishek.studentms.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.abhishek.studentms.model.Project;
import com.abhishek.studentms.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Manage projects independently of students")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@PostMapping
	@Operation(summary = "Create a project that is not yet assigned to a student")
	public ResponseEntity<Project> saveProject(@Valid @RequestBody Project project) {
		Project saved = projectService.saveProject(project);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId())
				.toUri();
		return ResponseEntity.created(location).body(saved);
	}

	@GetMapping
	@Operation(summary = "List all projects")
	public ResponseEntity<List<Project>> getAllProjects() {
		return ResponseEntity.ok(projectService.getAllProjects());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Find a project by id")
	public ResponseEntity<Project> findProjectById(@PathVariable Long id) {
		return ResponseEntity.ok(projectService.findProjectById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a project's name and/or description")
	public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
		return ResponseEntity.ok(projectService.updateProject(id, project));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a project by id")
	public ResponseEntity<Void> deleteProjectById(@PathVariable Long id) {
		projectService.deleteProjectById(id);
		return ResponseEntity.noContent().build();
	}

}
