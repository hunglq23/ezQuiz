package com.group3.ezquiz.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;

public interface IQuestionService {

  Question createNewQuestionOfQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

  Integer getTrueOrFalseAnswerNumberInQuestion(Long id, Boolean b);

  Question getByIdAndQuiz(Long questId, Quiz quiz);

  ResponseEntity<?> checkQuestionAnswers(Long id, Map<String, String> params, String questIndex);

}
