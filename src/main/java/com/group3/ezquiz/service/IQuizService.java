package com.group3.ezquiz.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IQuizService {

  Quiz getDraftQuiz(HttpServletRequest request);

  Quiz getQuizByRequestAndID(HttpServletRequest request, UUID id);

  Quiz handleQuestionCreatingInQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

  ResponseEntity<?> handleQuizUpdatingRequest(HttpServletRequest request, UUID id, @Valid QuizDetailsDto dto);

}
