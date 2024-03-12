package com.group3.ezquiz.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.utils.Utility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
  public Page<QuizDto> getQuizInLibrary(
      HttpServletRequest http,
      String sortOrder,
      Boolean isDraft,

      Pageable pageable) {
    User userRequesting = userService.getUserRequesting(http);
    Page<Quiz> quizByCreator;
    if (isDraft != null) {
      quizByCreator = quizRepo.findByCreatorAndIsDraftAndSort(userRequesting, isDraft, sortOrder, pageable);
    } else {
      quizByCreator = quizRepo.findByCreatorAndSort(userRequesting, sortOrder, pageable);
    }
    Page<QuizDto> quizDtoList = quizByCreator.map(this::mapToQuizDto);

    quizDtoList.forEach(objectDto -> objectDto.setTimeString(
        Utility.calculateTimeElapsed(
            Utility.convertStringToTimestamp(objectDto.timeString(), "yyyy-MM-dd HH:mm:ss"))));
    return quizDtoList;
  }

  private QuizDto mapToQuizDto(Quiz quiz) {
    return QuizDto.builder()
            .id(quiz.getId())
            .type("Quiz")
            .title(quiz.getTitle())
            .description(quiz.getDescription())
            .image(quiz.getImageUrl())
            .isDraft(quiz.getIsDraft())
            .itemNumber(quiz.getQuestions().size())
            .timeString(quiz.getCreatedAt().toString())
            .build();
  }

  @Override
  public Quiz findQuizById(UUID id) {
    Quiz quizById = quizRepo.findQuizById(id);
    if (quizById != null) {
      return quizById;
    }
    throw new ResourceNotFoundException("Cannot find quiz with" + id);
  }

  @Override
  public void deleteQuiz(UUID id) {
    Optional<Quiz> optionalQuiz = quizRepo.findById(id);
    if (optionalQuiz.isPresent()) {
      Quiz existedQuiz = optionalQuiz.get();
      quizRepo.delete(existedQuiz);
    } else {
      throw new ResourceNotFoundException("Quiz with id " + id + "not found!");
    }
  }
}
