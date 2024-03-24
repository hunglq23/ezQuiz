package com.group3.ezquiz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.group3.ezquiz.model.Attempt;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;

public interface AttemptRepo extends JpaRepository<Attempt, Long> {

  Integer countByQuizTaking_LearnerAndQuizTaking_Quiz(User learner, Quiz quiz);

  Optional<Attempt> findTopByQuizTaking_LearnerAndQuizTaking_QuizOrderByStartedAtDesc(User learner, Quiz quiz);

  Attempt findTopByQuizTaking_LearnerAndQuizTaking_QuizOrderByResultDesc(User learner, Quiz quiz);

  Integer countByIdAndResponses_Question(Long id, Question question);

  Optional<Attempt> findByIdAndQuizTaking_LearnerAndQuizTaking_QuizOrderByStartedAtDesc(
      Long id, User learner, Quiz quiz);

  @Query("SELECT a FROM Attempt a " +
      "WHERE a.result IS NOT NULL " +
      "AND a.quizTaking.learner = :learner " +
      "AND a.quizTaking.quiz = :quiz " +
      "ORDER BY a.startedAt DESC " +
      "LIMIT 1")
  Attempt findLastFinishedAttempt(User learner, Quiz quiz);

  Integer countByResultIsNotNullAndQuizTaking_LearnerAndQuizTaking_Quiz(User learner, Quiz quiz);

}
