package com.group3.ezquiz.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.payload.QuestionDto;

import jakarta.servlet.http.HttpServletRequest;

public interface IQuestionService {

    void createNewQuestion(HttpServletRequest request, QuestionDto dto, Map<String, String> params);

    List<Question> searchQuestionsByText(String searchText);

    List<Question> getAllQuestions();

    void updateQuestion(Long id, Question question) throws NotFoundException;

    void deleteQuestion(Long id);

    public void toggleQuestionStatus(Long questionId);

}
