package com.group3.ezquiz.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.InvalidQuestionException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.question.CorrectQuestion;
import com.group3.ezquiz.payload.question.IncorrectQuestion;
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
        .quizList(new ArrayList<>(List.of(quiz)))
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
    for (Entry<String, String> entry : params.entrySet()) {
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

  @Override
  public Integer getCorrectAnswerNumberInQuestion(Long questId) {

    return questionRepo.countByIdAndAnswers_IsCorrectIsTrue(questId);
  }

  @Override
  public Question getByIdAndQuiz(Long questId, Quiz quiz) {

    return questionRepo
        .findByIdAndQuizList_Id(questId, quiz.getId())
        .orElseThrow(
            () -> new ResourceNotFoundException(
                "Not found question ID (" + questId +
                    ") in quiz ID: " + quiz.getId()));
  }

  @Override
  public ResponseEntity<?> checkQuestionAnswers(
      Long questionId,
      Map<String, String> uncheckAnswers,
      String questIndex) {

    Boolean allAnswerCorrect = true;
    IncorrectQuestion incorrectQuestion = new IncorrectQuestion(questIndex, new HashMap<>());

    for (Entry<String, String> ansEntry : uncheckAnswers.entrySet()) {
      if (questionRepo
          .findByIdAndAnswers_IdAndAnswers_IsCorrect(
              questionId,
              Long.parseLong(ansEntry.getKey()),
              Boolean.parseBoolean(ansEntry.getValue()))
          .isPresent() == false) {

        incorrectQuestion.getAnswers().put(ansEntry.getKey(), false);
        allAnswerCorrect = false;
      } else {
        incorrectQuestion.getAnswers().put(ansEntry.getKey(), true);
      }
    }

    if (allAnswerCorrect) {
      return ResponseEntity.ok(
          CorrectQuestion.builder()
              .message("All the answers in question are correct!")
              .questIndex(questIndex)
              .timestamp(LocalDateTime.now())
              .build());
    }

    return new ResponseEntity<>(incorrectQuestion, HttpStatus.BAD_REQUEST);
  }
}