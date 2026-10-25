package com.abhishek.studentms.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abhishek.studentms.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

	Optional<Student> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);

	List<Student> findByIndexNumber(Integer indexNumber);

	List<Student> findByDateOfBirthBetweenOrderByDateOfBirth(LocalDate from, LocalDate to);

	List<Student> findByDepartmentIgnoreCase(String department);

	@Query("SELECT s FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) "
			+ "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
	List<Student> searchByName(@Param("name") String name);

	long countByIsOnBudget(Boolean isOnBudget);

	@Query("SELECT COALESCE(s.department, 'Unassigned'), COUNT(s) FROM Student s GROUP BY s.department")
	List<Object[]> countByDepartment();

}
