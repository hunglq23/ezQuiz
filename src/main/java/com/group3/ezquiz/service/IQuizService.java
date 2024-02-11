package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Quiz;

import com.group3.ezquiz.payload.QuizDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IQuizService {

    void createQuiz(HttpServletRequest request, QuizDto quizDto);

    Quiz findQuizById(Integer id);

    void deleteQuiz(Integer id);

    void updateQuiz(HttpServletRequest http, Integer id, QuizDto updateQuiz);

    Page<Quiz> listAll(HttpServletRequest http, String searchTerm, Pageable pageable);

    void toggleQuizStatus(Integer id);

    boolean existedQuizByCode(String code);

    Quiz getQuizById(String id);
}
