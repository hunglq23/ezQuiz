package com.group3.ezquiz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Attempt;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizTaking;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.model.UserResponse;
import com.group3.ezquiz.model.enums.Status;
import com.group3.ezquiz.payload.quiz.attempt.AttemptDto;
import com.group3.ezquiz.repository.AttemptRepo;
import com.group3.ezquiz.service.IAttemptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements IAttemptService {

  private final static Logger log = LoggerFactory.getLogger(AttemptServiceImpl.class);

  private final AttemptRepo attemptRepo;

  @Override
  public AttemptDto getNewAttempt(User learner, Quiz quizById) {

    Attempt newAttempt = Attempt.builder()
        .totalQuestNum(quizById.getQuestions().size())
        .correctNum(0)
        .incorrectNum(0)
        .build();

    int takenAttemptCount = countAllTakenAttempt(learner, quizById);
    AttemptDto attemptDto = AttemptDto.builder()
        .currentAttempt(takenAttemptCount + 1)
        .build();
    if (takenAttemptCount == 0) {
      QuizTaking quizTaking = QuizTaking.builder()
          .learner(learner)
          .quiz(quizById)
          .status(Status.ONGOING)
          .build();
      newAttempt.setQuizTaking(quizTaking);

      quizById.getQuizTakingList().add(quizTaking);
      learner.getTakenQuizList().add(quizTaking);

    } else {

      Attempt lastAttempt = getLastAttemptByLearnerAndQuiz(learner, quizById);
      if (lastAttempt.getEndedAt() == null) {
        saveResultAndfinishAttempt(lastAttempt);
      }

      attemptDto.setLastTaking(lastAttempt.getEndedAt());
      newAttempt.setQuizTaking(lastAttempt.getQuizTaking());

      if (takenAttemptCount == 1) {
        // case the best result attempt is literally the last attempt
        attemptDto.setBestResult(lastAttempt.getResult());
      } else {
        Attempt bestResultAttempt = findBestResultAttemptByLearnerAndQuiz(learner, quizById);
        attemptDto.setBestResult(bestResultAttempt.getResult());
      }
    }
    Attempt savedAttempt = attemptRepo.save(newAttempt);
    attemptDto.setId(savedAttempt.getId());
    return attemptDto;
  }

  @Override
  public Attempt saveResultAndfinishAttempt(Attempt attempt) {
    double correctNum = attempt.getCorrectNum();
    double totalQuestNum = attempt.getTotalQuestNum();
    BigDecimal result = BigDecimal.valueOf(0);
    if (totalQuestNum != 0) {
      double resultInDouble = correctNum / totalQuestNum * 100;
      result = BigDecimal.valueOf(resultInDouble)
          .setScale(2, RoundingMode.HALF_UP);
    }
    attempt.setResult(result);
    attempt.setEndedAt(Timestamp.valueOf(LocalDateTime.now()));
    return attemptRepo.save(attempt);
  }

  @Override
  public Attempt getLastAttemptByLearnerAndQuiz(User learner, Quiz quiz) {
    return attemptRepo
        .findTopByQuizTaking_LearnerAndQuizTaking_QuizOrderByStartedAtDesc(
            learner,
            quiz)
        .orElseThrow(() -> new ResourceNotFoundException("Attempt not found!"));
  }

  @Override
  public void addQuestNumWithValue(Attempt attempt, boolean isCorrect) {
    if (isCorrect) {
      int currentCorrectNum = attempt.getCorrectNum();
      attempt.setCorrectNum(currentCorrectNum + 1);
    } else {
      int curInCorrectNum = attempt.getIncorrectNum();
      attempt.setIncorrectNum(curInCorrectNum + 1);
    }
    attemptRepo.save(attempt);
  }

  @Override
  public Integer getAnsNumSelected(Attempt attempt, Long answerId, Question question) {
    List<UserResponse> resps = attempt.getResponses();
    if (resps == null) {
      resps = new ArrayList<>();
    }
    resps.add(UserResponse.builder()
        .attempt(attempt)
        .question(question)
        .answer(Answer.builder().id(answerId).build())
        .build());
    attempt.setResponses(resps);
    attemptRepo.save(attempt);

    return attemptRepo.countByIdAndResponses_Question(attempt.getId(), question);
  }

  @Override
  public BigDecimal getBestResult(Quiz quiz, User learner) {
    Attempt attempt = attemptRepo.findTopByQuizTaking_LearnerAndQuizTaking_QuizOrderByResultDesc(learner, quiz);
    if (attempt == null) {
      log.error("Not found best result", new ResourceNotFoundException("Not found best result"));
    }
    return attempt.getResult();
  }

  private Attempt findBestResultAttemptByLearnerAndQuiz(User learner, Quiz quiz) {
    return attemptRepo
        .findTopByQuizTaking_LearnerAndQuizTaking_QuizOrderByResultDesc(
            learner, quiz);
  }

  private Integer countAllTakenAttempt(User learner, Quiz quiz) {
    Integer takenTime = attemptRepo.countByQuizTaking_LearnerAndQuizTaking_Quiz(learner, quiz);
    log.info("Taken time was count: " + takenTime);
    return takenTime;
  }
}
