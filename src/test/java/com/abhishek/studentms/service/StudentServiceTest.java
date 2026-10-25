package com.abhishek.studentms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abhishek.studentms.exception.EmailInUseException;
import com.abhishek.studentms.exception.EmptyInputException;
import com.abhishek.studentms.exception.ProjectNotFoundException;
import com.abhishek.studentms.exception.StudentNotFoundException;
import com.abhishek.studentms.model.Project;
import com.abhishek.studentms.model.Student;
import com.abhishek.studentms.model.StudentStats;
import com.abhishek.studentms.repository.ProjectRepository;
import com.abhishek.studentms.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private ProjectRepository projectRepository;

	private StudentService studentService;

	private Student aarav;

	@BeforeEach
	void setUp() {
		studentService = new StudentService(studentRepository, projectRepository);
		aarav = new Student(1L, "Aarav", "Sharma", LocalDate.of(2002, 4, 14), "aarav.sharma@example.com", 1001,
				"Computer Science", true);
	}

	@Test
	void saveStudentPersistsNewStudent() {
		when(studentRepository.existsByEmailIgnoreCase(aarav.getEmail())).thenReturn(false);
		when(studentRepository.save(aarav)).thenReturn(aarav);

		assertThat(studentService.saveStudent(aarav)).isSameAs(aarav);
		verify(studentRepository).save(aarav);
	}

	@Test
	void saveStudentRejectsDuplicateEmail() {
		when(studentRepository.existsByEmailIgnoreCase(aarav.getEmail())).thenReturn(true);

		assertThatThrownBy(() -> studentService.saveStudent(aarav)).isInstanceOf(EmailInUseException.class);
		verify(studentRepository, never()).save(any());
	}

	@Test
	void saveStudentRejectsNull() {
		assertThatThrownBy(() -> studentService.saveStudent(null)).isInstanceOf(EmptyInputException.class);
	}

	@Test
	void getAllStudentsFiltersByDepartment() {
		when(studentRepository.findByDepartmentIgnoreCase("Computer Science")).thenReturn(List.of(aarav));

		assertThat(studentService.getAllStudents(" Computer Science ")).containsExactly(aarav);
	}

	@Test
	void getAllStudentsReturnsEmptyListWhenNoneExist() {
		when(studentRepository.findAll()).thenReturn(List.of());

		assertThat(studentService.getAllStudents(null)).isEmpty();
	}

	@Test
	void findStudentByIdThrowsWhenMissing() {
		when(studentRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> studentService.findStudentById(99L)).isInstanceOf(StudentNotFoundException.class)
				.hasMessage("Student with id 99 does not exist.");
	}

	@Test
	void findStudentByIdRejectsInvalidId() {
		assertThatThrownBy(() -> studentService.findStudentById(0L)).isInstanceOf(EmptyInputException.class);
		assertThatThrownBy(() -> studentService.findStudentById(null)).isInstanceOf(EmptyInputException.class);
	}

	@Test
	void findStudentByEmailRejectsBlank() {
		assertThatThrownBy(() -> studentService.findStudentByEmail(" ")).isInstanceOf(EmptyInputException.class);
	}

	@Test
	void findStudentsByIndexNumberThrowsWhenNoneFound() {
		when(studentRepository.findByIndexNumber(42)).thenReturn(List.of());

		assertThatThrownBy(() -> studentService.findStudentsByIndexNumber(42))
				.isInstanceOf(StudentNotFoundException.class);
	}

	@Test
	void getStudentsBetweenTwoDobSwapsReversedDates() {
		LocalDate early = LocalDate.of(2000, 1, 1);
		LocalDate late = LocalDate.of(2003, 1, 1);
		when(studentRepository.findByDateOfBirthBetweenOrderByDateOfBirth(early, late)).thenReturn(List.of(aarav));

		assertThat(studentService.getStudentsBetweenTwoDOB(late, early)).containsExactly(aarav);
	}

	@Test
	void updateStudentOnlyChangesProvidedFields() {
		when(studentRepository.findById(1L)).thenReturn(Optional.of(aarav));
		when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

		Student changes = new Student();
		changes.setFirstName("Aaru");
		changes.setIsOnBudget(false);

		Student updated = studentService.updateStudentById(changes, 1L);

		assertThat(updated.getFirstName()).isEqualTo("Aaru");
		assertThat(updated.getIsOnBudget()).isFalse();
		assertThat(updated.getLastName()).isEqualTo("Sharma");
		assertThat(updated.getEmail()).isEqualTo("aarav.sharma@example.com");
	}

	@Test
	void updateStudentRejectsEmailOwnedBySomeoneElse() {
		when(studentRepository.findById(1L)).thenReturn(Optional.of(aarav));
		when(studentRepository.existsByEmailIgnoreCase("priya.verma@example.com")).thenReturn(true);

		Student changes = new Student();
		changes.setEmail("priya.verma@example.com");

		assertThatThrownBy(() -> studentService.updateStudentById(changes, 1L))
				.isInstanceOf(EmailInUseException.class);
	}

	@Test
	void deleteStudentByIdThrowsWhenMissing() {
		when(studentRepository.existsById(5L)).thenReturn(false);

		assertThatThrownBy(() -> studentService.deleteStudentById(5L)).isInstanceOf(StudentNotFoundException.class);
		verify(studentRepository, never()).deleteById(any());
	}

	@Test
	void deleteStudentByIdDeletesWhenPresent() {
		when(studentRepository.existsById(1L)).thenReturn(true);

		studentService.deleteStudentById(1L);

		verify(studentRepository).deleteById(1L);
	}

	@Test
	void deleteStudentByEmailDeletesMatchingStudent() {
		when(studentRepository.findByEmailIgnoreCase(aarav.getEmail())).thenReturn(Optional.of(aarav));

		studentService.deleteStudentByEmail(aarav.getEmail());

		verify(studentRepository).delete(aarav);
	}

	@Test
	void createProjectForStudentLinksProjectToStudent() {
		Project project = new Project(null, "Chat App", "WebSocket chat");
		when(studentRepository.findById(1L)).thenReturn(Optional.of(aarav));
		when(projectRepository.save(project)).thenReturn(project);

		Project saved = studentService.createProjectForStudent(1L, project);

		assertThat(saved.getStudent()).isSameAs(aarav);
	}

	@Test
	void getProjectForStudentThrowsWhenProjectBelongsToSomeoneElse() {
		when(studentRepository.findById(1L)).thenReturn(Optional.of(aarav));
		when(projectRepository.findByIdAndStudentId(7L, 1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> studentService.getProjectForStudent(1L, 7L))
				.isInstanceOf(ProjectNotFoundException.class);
	}

	@Test
	void getStatsAggregatesCounts() {
		when(studentRepository.count()).thenReturn(5L);
		when(studentRepository.countByIsOnBudget(true)).thenReturn(3L);
		when(studentRepository.countByDepartment())
				.thenReturn(List.of(new Object[] { "Computer Science", 2L }, new Object[] { "Civil", 3L }));

		StudentStats stats = studentService.getStats();

		assertThat(stats.totalStudents()).isEqualTo(5);
		assertThat(stats.onBudget()).isEqualTo(3);
		assertThat(stats.selfFinanced()).isEqualTo(2);
		assertThat(stats.byDepartment()).containsEntry("Computer Science", 2L).containsEntry("Civil", 3L);
	}

}
