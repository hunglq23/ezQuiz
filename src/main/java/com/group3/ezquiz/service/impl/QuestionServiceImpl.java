package com.group3.ezquiz.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.InvalidQuestionException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.repository.QuestionRepo;
import com.group3.ezquiz.service.IQuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements IQuestionService {

  private final static Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);

  private final QuestionRepo questionRepo;

  @Override
  public Question createNewQuestionOfQuiz(
      Quiz quiz,
      String type,
      String questionText,
      Map<String, String> params) {

    Question question = Question.builder()
        .type(type)
        .text(questionText)
        .creator(quiz.getCreator())
        .isActive(true)
        .isPublic(true)
        .build();

    List<Answer> answers = mapParamsToAnswers(params, question, type);

    question.setAnswers(answers);

    return questionRepo.save(question);
  }

  private List<Answer> mapParamsToAnswers(Map<String, String> params, Question question, String type) {

    List<Answer> answers = new ArrayList<>();
    int correctAnswerCount = 0;

    Boolean ansValue = false;
    // iterate through the map of all the answer
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String key = entry.getKey();
      if (key.startsWith("ans")) {
        if (key.endsWith("Value")) {
          try {
            ansValue = Boolean.parseBoolean(entry.getValue());
            if (ansValue)
              correctAnswerCount++;
          } catch (NumberFormatException e) {
            log.error("Invalid answer value!");
          }
        } else if (key.endsWith("Text")) {
          answers.add(
              Answer.builder()
                  .question(question)
                  .text(entry.getValue())
                  .isCorrect(ansValue)
                  .build());
          ansValue = false;
        }
      }
    }
    if (type.equals("single-choice") && correctAnswerCount < 1) {
      throw new InvalidQuestionException("At least 1 answer selected in single choice!");
    }
    if (type.equals("multiple-choice") && correctAnswerCount < 2) {
      throw new InvalidQuestionException("At least 2 answer selected in multiple choice!");
    }
    return answers;
  }

}
