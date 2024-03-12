package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Question;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Long> {

  @Query("SELECT COUNT(*) FROM Question q " +
      "LEFT JOIN Answer a " + "ON" + "(q.id = a.question.id) " +
      "WHERE" + "(q.id = :id)" +
      "AND" + "(a.isCorrect = :aValue)")
  Integer findTrueOfFlaseAnswerNumberInQuestion(Long id, Boolean aValue);

  Optional<Question> findByIdAndQuizList_Id(Long questionId, UUID quizId);

  Optional<Question> findByIdAndAnswers_IdAndAnswers_IsCorrect(
      Long questionId, Long answerId, Boolean answerValue);

}
