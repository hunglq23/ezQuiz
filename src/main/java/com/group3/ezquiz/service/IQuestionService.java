package com.group3.ezquiz.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.group3.ezquiz.model.Question;

public interface IQuestionService {

    Question createQuestion(Question question);

    List<Question> getAllQuestions();

    Optional<Question> getQuestionById(Integer id);

    Question updateQuestion(Integer id, Question question) throws NotFoundException;

    void deleteQuestion(Integer id);

    List<Question> getQuestionsForUser(Integer numOfQuestion);

}
