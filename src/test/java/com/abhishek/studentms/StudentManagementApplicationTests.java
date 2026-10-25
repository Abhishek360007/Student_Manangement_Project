package com.abhishek.studentms;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * End-to-end tests against the real H2 database loaded with db/data.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class StudentManagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void healthCheckAndHomePageArePublic() throws Exception {
		mockMvc.perform(get("/actuator/health")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"));
		mockMvc.perform(get("/")).andExpect(status().is3xxRedirection());
	}

	@Test
	void seedDataIsLoaded() throws Exception {
		mockMvc.perform(get("/api/v1/students").with(httpBasic("admin", "admin123"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", Matchers.greaterThanOrEqualTo(5)));
	}

	@Test
	void searchAndStatsWork() throws Exception {
		mockMvc.perform(get("/api/v1/students/search").param("name", "pat").with(httpBasic("admin", "admin123")))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].lastName").value("Patel"));

		mockMvc.perform(get("/api/v1/students/stats").with(httpBasic("admin", "admin123")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.byDepartment['Computer Science']").value(2));
	}

	@Test
	void fullStudentAndProjectLifecycle() throws Exception {
		String student = """
				{"firstName":"Kabir","lastName":"Mehta","dateOfBirth":"2003-07-19",
				 "email":"kabir.mehta@example.com","indexNumber":2001,"department":"Physics","isOnBudget":false}
				""";
		String location = mockMvc
				.perform(post("/api/v1/students").with(httpBasic("admin", "admin123"))
						.contentType(MediaType.APPLICATION_JSON).content(student))
				.andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
		String path = location.substring(location.indexOf("/api"));

		// Duplicate email is rejected
		mockMvc.perform(post("/api/v1/students").with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON).content(student)).andExpect(status().isConflict());

		mockMvc.perform(post(path + "/projects").with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"projectName\":\"Optics Lab\",\"projectDescription\":\"Laser interference experiments\"}"))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.projectName").value("Optics Lab"));

		mockMvc.perform(get(path + "/projects").with(httpBasic("admin", "admin123"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));

		// Deleting the student also deletes their projects
		mockMvc.perform(delete(path).with(httpBasic("admin", "admin123"))).andExpect(status().isNoContent());
		mockMvc.perform(get(path).with(httpBasic("admin", "admin123"))).andExpect(status().isNotFound());
	}

}
