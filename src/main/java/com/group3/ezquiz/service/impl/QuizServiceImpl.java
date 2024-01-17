package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuizRequest;
import com.group3.ezquiz.repository.QuizRepository;
import com.group3.ezquiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    @Override
    public List<Quiz> listAll() {
        return (List<Quiz>)quizRepository.findAll();
    }

    @Override
    public void createQuiz(QuizRequest reqQuiz) {

    }

}
