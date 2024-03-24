package com.group3.ezquiz.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.group3.ezquiz.model.Attempt;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;

public interface IQuestionService {

  Question createNewQuestionOfQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

  Integer getCorrectAnswerNumberInQuestion(Long id);

  Question getByIdAndQuiz(Long questId, Quiz quiz);

  ResponseEntity<?> checkQuestionAnswers(Attempt lastAttempt, Long id, Map<String, String> params, String questIndex);

  Question getQuestionOfAnswerId(Long answerId, UUID quizId);

}
