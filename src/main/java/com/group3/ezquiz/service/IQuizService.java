package com.group3.ezquiz.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.group3.ezquiz.payload.quiz.QuizDetail;
import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.AssignedQuizDto;
import com.group3.ezquiz.payload.HomeContent;
import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.QuizReqParam;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizToLearner;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.payload.quiz.QuizResult;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IQuizService {

	Quiz getDraftQuiz(HttpServletRequest request);

	Quiz getQuizByRequestAndID(HttpServletRequest request, UUID id);

	QuizDetail getQuizWhenSearch(UUID id);

	Quiz handleQuestionCreatingInQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

	ResponseEntity<?> handleQuizUpdatingRequest(HttpServletRequest request, UUID id, @Valid QuizDetailsDto dto);

	QuizToLearner getQuizByLearnerForTaking(HttpServletRequest request, UUID id);

	ResponseEntity<?> handleAnswersChecking(
			HttpServletRequest request, UUID quizId, Long questId, Long questId2,
			String questIndex,
			Map<String, String> params);

	List<Question> importQuizDataFromExcel(HttpServletRequest request, MultipartFile excelFile, UUID id);

	ByteArrayInputStream getDataDownloaded(Quiz quiz) throws IOException;

	Page<QuizDto> getQuizInLibrary(HttpServletRequest http, String sortOrder, Boolean isDraft, Pageable pageable);

	List<QuizDto> searchQuizUUID(HttpServletRequest request, String search);

	void deleteQuiz(UUID id);

	Question getQuestionByIdAndQuiz(Long questionId, Quiz quiz);

	Quiz handleQuestionEditingInQuiz(Quiz quiz, Long questionId, String type, String questionText,
			Map<String, String> params);

	void deleteQuestionById(UUID id, Long questionId);

	void assignQuiz(HttpServletRequest request, UUID quizId, AssignedQuizDto assignedQuizDTO);

	ResponseEntity<?> handleAnswerSelected(HttpServletRequest request, UUID quizId, Long answerId, Long answerId2);

	void handleFinishQuizAttempt(HttpServletRequest request, UUID quizId, Long attemptId);

	QuizResult findLastFinishAttemptResult(HttpServletRequest request, UUID quizId);

	Page<QuizDto> getCreatedQuizList(HttpServletRequest request, @Valid QuizReqParam params);

	Page<QuizDto> getAvailableQuizList(@Valid LibraryReqParam params);

	HomeContent getHomeContent();

	Quiz getQuizById(UUID id);

}
