package com.group3.ezquiz.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.payload.QuestionDto;

public interface IQuestionService {

    void createNewQuestion(QuestionDto dto, Map<String, String> params);

    List<Question> getAllQuestions();

    Optional<Question> getQuestionById(Long id);

    Question updateQuestion(Long id, Question question) throws NotFoundException;

    void deleteQuestion(Long id);

    List<Question> getQuestionsForUser(Long numOfQuestion);

    public void toggleQuestionStatus(Long questionId);

}
