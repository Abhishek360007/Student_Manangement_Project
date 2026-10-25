package com.abhishek.studentms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "students")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "first_name", nullable = false)
	@NotBlank(message = "{student.firstName.notBlank}")
	@Size(max = 50, message = "{student.firstName.maxSize}")
	private String firstName;

	@Column(name = "last_name", nullable = false)
	@NotBlank(message = "{student.lastName.notBlank}")
	@Size(max = 50, message = "{student.lastName.maxSize}")
	private String lastName;

	@Column(name = "date_of_birth", nullable = false)
	@NotNull(message = "{student.dateOfBirth.notNull}")
	@Past(message = "{student.dateOfBirth.past}")
	private LocalDate dateOfBirth;

	@Column(name = "email", nullable = false, unique = true)
	@NotBlank(message = "{student.email.notBlank}")
	@Size(max = 254, message = "{student.email.maxSize}")
	@Email(message = "{student.email.notValid}")
	private String email;

	@Column(name = "index_number", nullable = false)
	@NotNull(message = "{student.indexNumber.notNull}")
	@Min(value = 1, message = "{student.indexNumber.range}")
	@Max(value = 99999999, message = "{student.indexNumber.range}")
	private Integer indexNumber;

	@Column(name = "department")
	@Size(max = 100, message = "{student.department.maxSize}")
	private String department;

	@Column(name = "is_on_budget", nullable = false)
	@NotNull(message = "{student.isOnBudget.notNull}")
	private Boolean isOnBudget;

	@OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Project> projects = new ArrayList<>();

	public Student() {
	}

	public Student(Long id, String firstName, String lastName, LocalDate dateOfBirth, String email, Integer indexNumber,
			String department, Boolean isOnBudget) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.email = email;
		this.indexNumber = indexNumber;
		this.department = department;
		this.isOnBudget = isOnBudget;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIndexNumber() {
		return indexNumber;
	}

	public void setIndexNumber(Integer indexNumber) {
		this.indexNumber = indexNumber;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Boolean getIsOnBudget() {
		return isOnBudget;
	}

	public void setIsOnBudget(Boolean isOnBudget) {
		this.isOnBudget = isOnBudget;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", dateOfBirth="
				+ dateOfBirth + ", email=" + email + ", indexNumber=" + indexNumber + ", department=" + department
				+ ", isOnBudget=" + isOnBudget + "]";
	}

}
