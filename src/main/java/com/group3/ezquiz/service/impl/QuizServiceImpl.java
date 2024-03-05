package com.group3.ezquiz.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.QuizRepo;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;
import com.group3.ezquiz.service.IQuestionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements IQuizService {

  private final QuizRepo quizRepo;
  private final IQuestionService questionService;
  private final IUserService userService;

  @Override
  public Quiz getDraftQuiz(HttpServletRequest request) {
    User userRequesting = userService.getUserRequesting(request);
    Quiz quiz = Quiz.builder()
        .isDraft(true)
        .isEnable(true)
        .isExam(false)
        .creator(userRequesting)
        .build();
    Quiz saved = quizRepo.save(quiz);
    return saved;
  }

  @Override
  public Quiz getQuizByRequestAndID(HttpServletRequest request, UUID id) {
    User userRequesting = userService.getUserRequesting(request);
    return quizRepo
        .findByIdAndCreator(id, userRequesting)
        .orElseThrow(
            () -> new ResourceNotFoundException("Not found your quiz with ID " + id));
  }

  @Override
  public Quiz handleQuestionCreatingInQuiz(
      Quiz quiz,
      String type,
      String questionText,
      Map<String, String> params) {
    Question newQuestion = questionService
        .createNewQuestionOfQuiz(quiz, type, questionText, params);
    quiz.getQuestions().add(newQuestion);
    quizRepo.save(quiz);
    return quizRepo.save(quiz);
  }

}
