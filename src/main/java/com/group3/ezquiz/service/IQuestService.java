package com.group3.ezquiz.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.group3.ezquiz.model.Quest;

import jakarta.servlet.http.HttpServletRequest;

public interface IQuestService {

  Quest createNewQuestion(HttpServletRequest request, String type, String questionText, Map<String, String> answerMap);

  List<Quest> getAllPublicQuestion();

  List<Quest> getCreatedQuestions(HttpServletRequest request);

  Quest getCreatedQuestionById(HttpServletRequest request, String id);

  ResponseEntity<?> handleEditReqAndGetUpdatedQuest(HttpServletRequest request, String id, String type,
      String questionText,
      Map<String, String> params);

}
