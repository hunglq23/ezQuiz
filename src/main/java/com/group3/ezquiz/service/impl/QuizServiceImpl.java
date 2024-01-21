package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.repository.QuizRepository;
import com.group3.ezquiz.service.QuizService;
import com.group3.ezquiz.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserService userService;

    @Override
    public List<Quiz> listAll() {
        return (List<Quiz>) quizRepository.findAll();
    }

    @Override
    public void createQuiz(HttpServletRequest request, QuizDto quizDto) {

        quizRepository.save(
                Quiz.builder()
                        .code(quizDto.getCode())
                        .title(quizDto.getTitle())
                        .description(quizDto.getDescription())
                        .isExamOnly(quizDto.getIsExamOnly())
                        .isAcitve(quizDto.getIsActive())
                        .createdBy(userService.getUserRequesting(request))
                        .build());
    }

}
