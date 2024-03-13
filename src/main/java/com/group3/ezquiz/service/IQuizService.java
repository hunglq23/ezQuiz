package com.group3.ezquiz.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizToLearner;
import com.group3.ezquiz.payload.quiz.QuizDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IQuizService {

  Quiz getDraftQuiz(HttpServletRequest request);

  Quiz getQuizByRequestAndID(HttpServletRequest request, UUID id);

  Quiz handleQuestionCreatingInQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

  ResponseEntity<?> handleQuizUpdatingRequest(HttpServletRequest request, UUID id, @Valid QuizDetailsDto dto);

  QuizToLearner getQuizByLearnerForQuizTaking(UUID id);

  ResponseEntity<?> handleAnswersChecking(UUID quizId, Long questId, String questIndex, Map<String, String> params);

  List<Question> importQuizDataFromExcel(HttpServletRequest request, MultipartFile excelFile, UUID id);

  ByteArrayInputStream getDataDownloaded(Quiz quiz) throws IOException;

  Page<QuizDto> getQuizInLibrary(HttpServletRequest http, String sortOrder, Boolean isDraft, Pageable pageable);

  Quiz findQuizById(UUID id);

  void deleteQuiz(UUID id);
}
