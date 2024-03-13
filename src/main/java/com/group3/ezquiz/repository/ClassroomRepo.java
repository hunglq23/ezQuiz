package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.group3.ezquiz.model.Classroom;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, Long> {
  List<Classroom> findByCreator(User creator);

  Optional<Classroom> findByCreatorAndName(User creator, String name);

  Optional<Classroom> findByIdAndCreator(Long id, User creator);

  Optional<Classroom> findByCode(String code);

}
