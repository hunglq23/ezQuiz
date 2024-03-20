package com.group3.ezquiz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.ezquiz.model.Attempt;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;

public interface AttemptRepo extends JpaRepository<Attempt, Long> {

  Integer countByQuizTaking_LearnerAndQuizTaking_Quiz(User learner, Quiz quiz);

  Optional<Attempt> findTopByQuizTaking_LearnerAndQuizTaking_QuizOrderByStartedAtDesc(User learner, Quiz quiz);

  Attempt findTopByQuizTaking_LearnerAndQuizTaking_QuizOrderByResultDesc(User learner, Quiz quiz);

  Integer countByIdAndResponses_Question(Long id, Question question);

}
