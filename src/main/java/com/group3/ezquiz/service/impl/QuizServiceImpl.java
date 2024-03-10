package com.group3.ezquiz.service.impl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.utils.Utility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
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
  public List<QuizDto> getQuizByCreator(HttpServletRequest http, String sortOrder, Boolean isDraft) {
    User userRequesting = userService.getUserRequesting(http);
    List<Quiz> quizByCreator;
    if (isDraft != null) {
      quizByCreator = quizRepo.findByCreatorAndIsDraft(userRequesting, isDraft);
    } else {
      quizByCreator = quizRepo.findByCreator(userRequesting);
    }
    List<QuizDto> quizDtoList = new ArrayList<>();
    for (Quiz quiz : quizByCreator) {
      quizDtoList.add(mapToQuizDto(quiz));
    }
    Comparator<QuizDto> comparator;
    switch (sortOrder){
      case "latest":
        comparator = Comparator.comparing(QuizDto::getTimeString).reversed();
        quizDtoList.sort(comparator);
        break;
      case "oldest":
        comparator = Comparator.comparing(QuizDto::getTimeString);
        quizDtoList.sort(comparator);
        break;
    }
    quizDtoList.forEach(objectDto -> objectDto.setTimeString(
            Utility.calculateTimeElapsed(
                    Utility.convertStringToTimestamp(objectDto.timeString(), "yyyy-MM-dd HH:mm:ss"))));
    return quizDtoList;
  }

  private QuizDto mapToQuizDto(Quiz quiz) {
    return QuizDto.builder()
            .type("Quiz")
            .title(quiz.getTitle())
            .description(quiz.getDescription())
            .image(quiz.getImageUrl())
            .isDraft(quiz.getIsDraft())
            .itemNumber(quiz.getQuestions().size())
            .timeString(quiz.getCreatedAt().toString())
            .build();
  }
}
