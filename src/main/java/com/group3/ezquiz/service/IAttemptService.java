package com.group3.ezquiz.service;

import java.math.BigDecimal;

import com.group3.ezquiz.model.Attempt;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.quiz.attempt.AttemptDto;

public interface IAttemptService {

  AttemptDto getNewAttempt(User learner, Quiz quizById);

  Attempt getLastAttemptByLearnerAndQuiz(User learner, Quiz quiz);

  void addQuestNumWithValue(Attempt attempt, boolean isCorrect);

  Integer getAnsNumSelected(Attempt attempt, Long answerId, Question question);

  Attempt saveResultAndfinishAttempt(Attempt attempt);

  BigDecimal getBestResult(Quiz quiz, User learner);

  Attempt findLastFinishedAttempt(User learner, Quiz quiz);

  Attempt getByIdAndLearnerAndQuiz(Long attemptId, User learner, Quiz quiz);

  Integer getCurrentFinishedAttemptNum(User learner, Quiz quiz);

}
