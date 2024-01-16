package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuizRequest;


import java.util.List;


public interface QuizService {

    List<Quiz> listAll();

//    void createQuiz(QuizRequest reqQuiz);
}
