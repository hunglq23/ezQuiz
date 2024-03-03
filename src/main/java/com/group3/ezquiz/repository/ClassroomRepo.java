package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, Long> {
  @Query("Select c from Classroom c where" +
      "(:sName is null or c.className Like %:sName%)")

  Page<Classroom> getAllClassroom(String sName, Pageable page);

  List<Classroom> findByCreator(User creator);

  Classroom findByCode(String code);

  Optional<Classroom> findByIdAndCreator(Long id, User creator);

}
