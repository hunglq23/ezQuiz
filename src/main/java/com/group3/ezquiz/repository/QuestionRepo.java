package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Long> {

    Integer countByIdAndAnswers_IsCorrectIsTrue(Long id);

    Optional<Question> findByIdAndQuizList_Id(Long questionId, UUID quizId);

    Optional<Question> findByIdAndAnswers_IdAndAnswers_IsCorrect(
            Long questionId, Long answerId, Boolean answerValue);

    List<Question> findByQuizList_IdAndAnswers_Id(UUID quizId, Long answerId);
}
