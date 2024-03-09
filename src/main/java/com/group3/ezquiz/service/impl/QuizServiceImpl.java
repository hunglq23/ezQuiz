package com.group3.ezquiz.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.InvalidQuestionException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.QuestionToLearner;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizToLearner;
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

  @Override
  public ResponseEntity<?> handleQuizUpdatingRequest(
      HttpServletRequest request,
      UUID id,
      QuizDetailsDto dto) {
    Quiz quiz = getQuizByRequestAndID(request, id);

    if (quiz.getQuestions().size() == 0) {
      return new ResponseEntity<>(
          MessageResponse.builder()
              .message("The number of questions must be greater than 0!")
              .timestamp(LocalDateTime.now())
              .build(),
          HttpStatus.BAD_REQUEST);
    }

    quiz.setImageUrl(dto.getImageUrl());
    quiz.setTitle(dto.getTitle());
    quiz.setIsExam(dto.getIsExam());
    quiz.setDescription(dto.getDescription());
    quiz.setIsDraft(false);

    quizRepo.save(quiz);

    return ResponseEntity.ok(
        MessageResponse.builder()
            .message("Saved successfully!")
            .timestamp(LocalDateTime.now())
            .build());
  }

  @Override
  public QuizToLearner getQuizByLearnerForQuizTaking(UUID id) {
    Quiz quizById = getQuizById(id);

    List<QuestionToLearner> questions = new ArrayList<>();

    Map<Long, String> answers;
    for (Question quest : quizById.getQuestions()) {
      answers = new HashMap<>();
      for (Answer answer : quest.getAnswers()) {
        answers.put(answer.getId(), answer.getText());
      }

      questions.add(
          QuestionToLearner.builder()
              .id(quest.getId())
              .text(quest.getText())
              .answers(answers)
              .numberOfCorrect(questionService
                  .getTrueOrFalseAnswerNumberInQuestion(quest.getId(), true))
              .build());
    }

    return QuizToLearner.builder()
        .id(quizById.getId())
        .title(quizById.getTitle())
        .questions(questions)
        .build();
  }

  @Override
  public ResponseEntity<?> handleAnswersChecking(
      UUID quizId,
      Long questId,
      String questIndex,
      Map<String, String> params) {
    Quiz quiz = getQuizById(quizId);
    Question uncheck = questionService.getByIdAndQuiz(questId, quiz);
    if (uncheck.getAnswers().size() != params.size()) {
      throw new InvalidQuestionException("Number of submited answers was wrong!");
    }
    return questionService
        .checkQuestionAnswers(uncheck.getId(), params, questIndex);
  }

  private Quiz getQuizById(UUID id) {
    return quizRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found quiz by ID " + id));
  }

}
