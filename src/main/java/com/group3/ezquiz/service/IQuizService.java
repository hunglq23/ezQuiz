package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.payload.QuizDetailsDto;
import com.group3.ezquiz.payload.QuizDto;
import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IQuizService {

        void createQuiz(HttpServletRequest request, QuizDto quizDto);

        Quiz findQuizById(Integer id);

        void deleteQuiz(Integer id);

        void updateQuiz(HttpServletRequest http, Integer id, QuizDto updateQuiz);

        Page<Quiz> listAll(HttpServletRequest http, String searchTerm, Pageable pageable);

        void toggleQuizStatus(Integer id);

        boolean existedQuizByCode(String code);

        Quiz getQuizById(String id);

        QuizUUID saveAndGetDraftQuiz(HttpServletRequest http);

        QuizUUID getQuizByRequestAndUUID(HttpServletRequest request, UUID id);

        ResponseEntity<?> handleQuizUpdatingRequest(
                        HttpServletRequest request, UUID id, QuizDetailsDto dto);

        String handleQuestionCreatingInQuiz(
                        HttpServletRequest request,
                        QuizUUID quiz,
                        String type,
                        String questionText,
                        Map<String, String> params);

        QuizUUID getQuizById(UUID id);

        void importQuizDataFromExcel(HttpServletRequest request, MultipartFile file, UUID id);

        ByteArrayInputStream getDataDownloaded(QuizUUID quiz) throws IOException;

}
