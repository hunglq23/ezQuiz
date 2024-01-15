package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    public List<Quiz> listAll(){
        return (List<Quiz>)quizRepository.findAll();
    }
}
