package com.abhishek.studentms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.abhishek.studentms.model.Project;
import com.abhishek.studentms.model.Student;
import com.abhishek.studentms.model.StudentStats;
import com.abhishek.studentms.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students", description = "Create, search, update and delete students")
public class StudentController {

	private final StudentService studentService;

	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	@PostMapping
	@Operation(summary = "Add a new student")
	public ResponseEntity<Student> saveStudent(@Valid @RequestBody Student student) {
		Student saved = studentService.saveStudent(student);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId())
				.toUri();
		return ResponseEntity.created(location).body(saved);
	}

	@GetMapping
	@Operation(summary = "List all students, optionally filtered by department")
	public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) String department) {
		return ResponseEntity.ok(studentService.getAllStudents(department));
	}

	@GetMapping("/{studentId}")
	@Operation(summary = "Find a student by id")
	public ResponseEntity<Student> findStudentById(@PathVariable Long studentId) {
		return ResponseEntity.ok(studentService.findStudentById(studentId));
	}

	@GetMapping("/email/{email}")
	@Operation(summary = "Find a student by email")
	public ResponseEntity<Student> findStudentByEmail(
			@PathVariable @Email(message = "{student.email.notValid}") String email) {
		return ResponseEntity.ok(studentService.findStudentByEmail(email));
	}

	@GetMapping("/index/{indexNumber}")
	@Operation(summary = "Find students by index number")
	public ResponseEntity<List<Student>> findStudentsByIndexNumber(
			@PathVariable @Min(value = 1, message = "{student.indexNumber.range}") @Max(value = 99999999, message = "{student.indexNumber.range}") Integer indexNumber) {
		return ResponseEntity.ok(studentService.findStudentsByIndexNumber(indexNumber));
	}

	@GetMapping("/search")
	@Operation(summary = "Search students by first or last name")
	public ResponseEntity<List<Student>> searchStudentsByName(@RequestParam String name) {
		return ResponseEntity.ok(studentService.searchStudentsByName(name));
	}

	@GetMapping("/date-of-birth")
	@Operation(summary = "Find students born between two dates (inclusive)")
	public ResponseEntity<List<Student>> getStudentsBetweenTwoDOB(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ResponseEntity.ok(studentService.getStudentsBetweenTwoDOB(from, to));
	}

	@GetMapping("/stats")
	@Operation(summary = "Student counts: total, on budget, self-financed and per department")
	public ResponseEntity<StudentStats> getStats() {
		return ResponseEntity.ok(studentService.getStats());
	}

	@PutMapping("/{studentId}")
	@Operation(summary = "Update a student - only the fields you send are changed")
	public ResponseEntity<Student> updateStudentById(@PathVariable Long studentId, @RequestBody Student student) {
		return ResponseEntity.ok(studentService.updateStudentById(student, studentId));
	}

	@DeleteMapping("/{studentId}")
	@Operation(summary = "Delete a student (and their projects) by id")
	public ResponseEntity<Void> deleteStudentById(@PathVariable Long studentId) {
		studentService.deleteStudentById(studentId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/email/{email}")
	@Operation(summary = "Delete a student (and their projects) by email")
	public ResponseEntity<Void> deleteStudentByEmail(
			@PathVariable @Email(message = "{student.email.notValid}") String email) {
		studentService.deleteStudentByEmail(email);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{studentId}/projects")
	@Operation(summary = "Add a project to a student")
	public ResponseEntity<Project> createProjectForStudent(@PathVariable Long studentId,
			@Valid @RequestBody Project project) {
		Project saved = studentService.createProjectForStudent(studentId, project);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId())
				.toUri();
		return ResponseEntity.created(location).body(saved);
	}

	@GetMapping("/{studentId}/projects")
	@Operation(summary = "List a student's projects")
	public ResponseEntity<List<Project>> getProjectsForStudent(@PathVariable Long studentId) {
		return ResponseEntity.ok(studentService.getProjectsForStudent(studentId));
	}

	@GetMapping("/{studentId}/projects/{projectId}")
	@Operation(summary = "Get one of a student's projects")
	public ResponseEntity<Project> getProjectForStudent(@PathVariable Long studentId, @PathVariable Long projectId) {
		return ResponseEntity.ok(studentService.getProjectForStudent(studentId, projectId));
	}

}
