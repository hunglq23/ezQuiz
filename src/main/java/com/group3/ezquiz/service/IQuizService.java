package com.group3.ezquiz.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;

import jakarta.servlet.http.HttpServletRequest;

public interface IQuizService {

  Quiz getDraftQuiz(HttpServletRequest request);

  Quiz getQuizByRequestAndID(HttpServletRequest request, UUID id);

  Quiz handleQuestionCreatingInQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

  List<Question> importQuizDataFromExcel(HttpServletRequest request, MultipartFile excelFile, UUID id);

  ByteArrayInputStream getDataDownloaded(Quiz quiz) throws IOException;

}
