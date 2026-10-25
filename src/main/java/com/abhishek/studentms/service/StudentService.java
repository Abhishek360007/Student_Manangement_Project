package com.abhishek.studentms.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhishek.studentms.exception.EmailInUseException;
import com.abhishek.studentms.exception.EmptyInputException;
import com.abhishek.studentms.exception.ProjectNotFoundException;
import com.abhishek.studentms.exception.StudentNotFoundException;
import com.abhishek.studentms.model.Project;
import com.abhishek.studentms.model.Student;
import com.abhishek.studentms.model.StudentStats;
import com.abhishek.studentms.repository.ProjectRepository;
import com.abhishek.studentms.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class StudentService {

	private final StudentRepository studentRepository;
	private final ProjectRepository projectRepository;

	public StudentService(StudentRepository studentRepository, ProjectRepository projectRepository) {
		this.studentRepository = studentRepository;
		this.projectRepository = projectRepository;
	}

	@Transactional
	public Student saveStudent(Student student) {
		if (student == null) {
			throw new EmptyInputException("Student details are required.");
		}
		if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) {
			throw new EmailInUseException("Email " + student.getEmail() + " is already in use.");
		}
		student.setId(null);
		return studentRepository.save(student);
	}

	public List<Student> getAllStudents(String department) {
		if (StringUtils.isNotBlank(department)) {
			return studentRepository.findByDepartmentIgnoreCase(department.trim());
		}
		return studentRepository.findAll();
	}

	public Student findStudentById(Long id) {
		requireId(id, "student");
		return studentRepository.findById(id)
				.orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " does not exist."));
	}

	public Student findStudentByEmail(String email) {
		if (StringUtils.isBlank(email)) {
			throw new EmptyInputException("Email is required.");
		}
		return studentRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new StudentNotFoundException("Student with email " + email + " does not exist."));
	}

	public List<Student> findStudentsByIndexNumber(Integer indexNumber) {
		if (indexNumber == null || indexNumber <= 0) {
			throw new EmptyInputException("Index number must be a positive number.");
		}
		List<Student> students = studentRepository.findByIndexNumber(indexNumber);
		if (students.isEmpty()) {
			throw new StudentNotFoundException("No student found with index number " + indexNumber + ".");
		}
		return students;
	}

	public List<Student> searchStudentsByName(String name) {
		if (StringUtils.isBlank(name)) {
			throw new EmptyInputException("Search text is required.");
		}
		return studentRepository.searchByName(name.trim());
	}

	public List<Student> getStudentsBetweenTwoDOB(LocalDate from, LocalDate to) {
		if (from == null || to == null) {
			throw new EmptyInputException("Both dates are required.");
		}
		if (from.isAfter(to)) {
			LocalDate tmp = from;
			from = to;
			to = tmp;
		}
		return studentRepository.findByDateOfBirthBetweenOrderByDateOfBirth(from, to);
	}

	@Transactional
	public Student updateStudentById(Student changes, Long id) {
		Student existing = findStudentById(id);
		if (changes == null) {
			return existing;
		}
		if (StringUtils.isNotBlank(changes.getFirstName())) {
			existing.setFirstName(changes.getFirstName());
		}
		if (StringUtils.isNotBlank(changes.getLastName())) {
			existing.setLastName(changes.getLastName());
		}
		if (changes.getDateOfBirth() != null) {
			if (!changes.getDateOfBirth().isBefore(LocalDate.now())) {
				throw new EmptyInputException("Date of birth must be in the past.");
			}
			existing.setDateOfBirth(changes.getDateOfBirth());
		}
		if (StringUtils.isNotBlank(changes.getEmail()) && !changes.getEmail().equalsIgnoreCase(existing.getEmail())) {
			if (studentRepository.existsByEmailIgnoreCase(changes.getEmail())) {
				throw new EmailInUseException("Email " + changes.getEmail() + " is already in use.");
			}
			existing.setEmail(changes.getEmail());
		}
		if (changes.getIndexNumber() != null && changes.getIndexNumber() > 0) {
			existing.setIndexNumber(changes.getIndexNumber());
		}
		if (changes.getDepartment() != null) {
			existing.setDepartment(StringUtils.trimToNull(changes.getDepartment()));
		}
		if (changes.getIsOnBudget() != null) {
			existing.setIsOnBudget(changes.getIsOnBudget());
		}
		return studentRepository.save(existing);
	}

	@Transactional
	public void deleteStudentById(Long id) {
		requireId(id, "student");
		if (!studentRepository.existsById(id)) {
			throw new StudentNotFoundException(
					"Student can not be deleted because student with id " + id + " does not exist.");
		}
		studentRepository.deleteById(id);
	}

	@Transactional
	public void deleteStudentByEmail(String email) {
		Student student = findStudentByEmail(email);
		studentRepository.delete(student);
	}

	@Transactional
	public Project createProjectForStudent(Long studentId, Project project) {
		Student student = findStudentById(studentId);
		project.setId(null);
		project.setStudent(student);
		return projectRepository.save(project);
	}

	public List<Project> getProjectsForStudent(Long studentId) {
		findStudentById(studentId);
		return projectRepository.findByStudentId(studentId);
	}

	public Project getProjectForStudent(Long studentId, Long projectId) {
		findStudentById(studentId);
		return projectRepository.findByIdAndStudentId(projectId, studentId)
				.orElseThrow(() -> new ProjectNotFoundException(
						"Project with id " + projectId + " does not exist for student " + studentId + "."));
	}

	public StudentStats getStats() {
		long total = studentRepository.count();
		long onBudget = studentRepository.countByIsOnBudget(true);
		Map<String, Long> byDepartment = new LinkedHashMap<>();
		for (Object[] row : studentRepository.countByDepartment()) {
			byDepartment.put((String) row[0], (Long) row[1]);
		}
		return new StudentStats(total, onBudget, total - onBudget, byDepartment);
	}

	private static void requireId(Long id, String entity) {
		if (id == null || id <= 0) {
			throw new EmptyInputException("A valid " + entity + " id is required.");
		}
	}

}
