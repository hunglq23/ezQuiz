package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Quiz;

import com.group3.ezquiz.payload.QuizDto;
import jakarta.servlet.http.HttpServletRequest;


import java.util.List;


public interface QuizService {

    List<Quiz> listAll();

    void createQuiz(HttpServletRequest request, QuizDto quizDto);
}
