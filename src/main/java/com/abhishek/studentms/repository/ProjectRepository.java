package com.abhishek.studentms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abhishek.studentms.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	List<Project> findByStudentId(Long studentId);

	Optional<Project> findByIdAndStudentId(Long id, Long studentId);

}
