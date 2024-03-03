package com.group3.ezquiz.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizUUIDRepo extends JpaRepository<QuizUUID, UUID> {

  QuizUUID findByIdAndCreator(UUID id, User creator);

  QuizUUID findQuizUUIDById(UUID id);

  @Query(value = "select q from QuizUUID q order by q.updatedAt DESC limit 3")
  List<QuizUUID> findQuizUUID();

  @Query(value = "select q from QuizUUID q where q.title like %:search%")
  List<QuizUUID> searchQuizUUID(@Param(value = "search") String search);

  List<QuizUUID> findByCreator(User creator);
}
