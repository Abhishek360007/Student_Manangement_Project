package com.abhishek.studentms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.abhishek.studentms.config.Config;
import com.abhishek.studentms.exception.StudentNotFoundException;
import com.abhishek.studentms.model.Student;
import com.abhishek.studentms.security.SecurityConfiguration;
import com.abhishek.studentms.service.StudentService;

@WebMvcTest(StudentController.class)
@Import({ SecurityConfiguration.class, Config.class })
class StudentControllerTest {

	private static final String BASE = "/api/v1/students";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private StudentService studentService;

	private final Student aarav = new Student(1L, "Aarav", "Sharma", LocalDate.of(2002, 4, 14),
			"aarav.sharma@example.com", 1001, "Computer Science", true);

	@Test
	void rejectsUnauthenticatedRequests() throws Exception {
		mockMvc.perform(get(BASE)).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser
	void listsStudents() throws Exception {
		when(studentService.getAllStudents(null)).thenReturn(List.of(aarav));

		mockMvc.perform(get(BASE)).andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("Aarav"))
				.andExpect(jsonPath("$[0].department").value("Computer Science"));
	}

	@Test
	@WithMockUser
	void findsStudentById() throws Exception {
		when(studentService.findStudentById(1L)).thenReturn(aarav);

		mockMvc.perform(get(BASE + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("aarav.sharma@example.com"));
	}

	@Test
	@WithMockUser
	void returns404WhenStudentMissing() throws Exception {
		when(studentService.findStudentById(9L)).thenThrow(new StudentNotFoundException("Student with id 9 does not exist."));

		mockMvc.perform(get(BASE + "/9")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Student with id 9 does not exist."))
				.andExpect(jsonPath("$.path").value(BASE + "/9"));
	}

	@Test
	@WithMockUser
	void createsStudent() throws Exception {
		when(studentService.saveStudent(any(Student.class))).thenReturn(aarav);

		String body = """
				{"firstName":"Aarav","lastName":"Sharma","dateOfBirth":"2002-04-14",
				 "email":"aarav.sharma@example.com","indexNumber":1001,"department":"Computer Science","isOnBudget":true}
				""";

		mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost" + BASE + "/1"))
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	@WithMockUser
	void rejectsInvalidStudentWithReadableMessage() throws Exception {
		String body = """
				{"firstName":"","lastName":"Sharma","dateOfBirth":"2002-04-14",
				 "email":"aarav.sharma@example.com","indexNumber":1001,"isOnBudget":true}
				""";

		mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("First name cannot be empty."));
		verify(studentService, never()).saveStudent(any());
	}

	@Test
	@WithMockUser
	void rejectsInvalidEmailPathVariable() throws Exception {
		mockMvc.perform(get(BASE + "/email/not-an-email")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Email is not valid."));
	}

	@Test
	@WithMockUser
	void updatesStudent() throws Exception {
		when(studentService.updateStudentById(any(Student.class), eq(1L))).thenReturn(aarav);

		mockMvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content("{\"department\":\"Computer Science\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
	}

	@Test
	@WithMockUser
	void deletesStudent() throws Exception {
		mockMvc.perform(delete(BASE + "/1")).andExpect(status().isNoContent());
		verify(studentService).deleteStudentById(1L);
	}

	@Test
	@WithMockUser
	void deletesStudentByEmail() throws Exception {
		mockMvc.perform(delete(BASE + "/email/aarav.sharma@example.com")).andExpect(status().isNoContent());
		verify(studentService).deleteStudentByEmail("aarav.sharma@example.com");
	}

}
