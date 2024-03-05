package com.group3.ezquiz.service;

import java.util.Map;
import java.util.UUID;

import com.group3.ezquiz.model.Quiz;

import jakarta.servlet.http.HttpServletRequest;

public interface IQuizService {

  Quiz getDraftQuiz(HttpServletRequest request);

  Quiz getQuizByRequestAndID(HttpServletRequest request, UUID id);

  Quiz handleQuestionCreatingInQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

}
