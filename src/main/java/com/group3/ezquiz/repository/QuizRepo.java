package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.Quiz;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.group3.ezquiz.model.User;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, UUID> {

  Optional<Quiz> findByIdAndCreator(UUID id, User creator);

    List<Quiz> findByCreatorAndIsDraft(User creator, Boolean isDraft);

    List<Quiz> findByCreator(User creator);

    // @Query("SELECT q FROM Quiz q WHERE " +
  // "(:code IS NULL OR q.code LIKE %:code%) OR " +
  // "(:title IS NULL OR q.title LIKE %:title%)")
  // Page<Quiz> getAllQuiz(String code, String title, Pageable page);
}
