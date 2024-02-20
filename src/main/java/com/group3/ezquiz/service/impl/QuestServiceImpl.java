package com.group3.ezquiz.service.impl;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Quest;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.IQuestRepo;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IQuestService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements IQuestService {

  private final static Logger log = LoggerFactory.getLogger(QuestServiceImpl.class);
  private final IQuestRepo questionRepo;
  private final UserRepo userRepo;

  private final Boolean QUESTION_IS_ACTIVE_DEFAULT = true;
  private final Boolean QUESTION_IS_PUBLIC_DEFAULT = true;

  @Override
  public Quest createNewQuestion(
      HttpServletRequest request,
      String type,
      String questionText,
      Map<String, String> params) {

    Quest question = Quest.builder()
        .type(type)
        .text(questionText)
        .creator(getUserRequesting(request))
        .isActive(QUESTION_IS_ACTIVE_DEFAULT)
        .isPublic(QUESTION_IS_PUBLIC_DEFAULT)
        .build();

    List<Answer> answers = mapParamsToAnswers(params, question);

    question.setAnswers(answers);

    return questionRepo.save(question);
  }

  @Override
  public List<Quest> getAllPublicQuestion() {
    Boolean isPublic = true;
    return questionRepo.findByIsPublic(isPublic);
  }

  @Override
  public List<Quest> getCreatedQuestions(HttpServletRequest request) {
    User userRequesting = getUserRequesting(request);
    return questionRepo.findByCreator(userRequesting);
  }

  @Override
  public Quest getCreatedQuestionById(HttpServletRequest request, String findId) {
    Quest question = null;

    Long id = getLongTypeId(findId);

    User userRequesting = getUserRequesting(request);
    question = questionRepo.findByIdAndCreator(id, userRequesting);

    // assert question not null
    checkIfNullCaseThrowNotFoundEx(question, findId);

    return question;
  }

  @Override
  public ResponseEntity<?> handleEditReqAndGetUpdatedQuest(
      HttpServletRequest request,
      String requestStringId,
      String requestType,
      String requestQuestionText,
      Map<String, String> params) {

    Quest existedQuestion = getCreatedQuestionById(request, requestStringId);
    List<Answer> requestAnswerList = mapParamsToAnswers(params, existedQuestion);
    List<Answer> existedAnswerList = existedQuestion.getAnswers();

    Boolean answerChanged = checkIfAnyAnswersChanged(requestAnswerList, existedAnswerList);

    if (!requestType.equals(existedQuestion.getType())
        || !requestQuestionText.equals(existedQuestion.getText())
        || answerChanged) {
      // question need updating
      existedQuestion.setType(requestType);
      existedQuestion.setText(requestQuestionText);
      if (answerChanged) {
        existedAnswerList.clear();
        existedAnswerList.addAll(requestAnswerList);
      }
      questionRepo.save(existedQuestion);
      return ResponseEntity.ok("Success!");
    }
    return ResponseEntity.badRequest().body("Nothing changed!");
  }

  private List<Answer> mapParamsToAnswers(Map<String, String> params, Quest question) {

    List<Answer> answers = new ArrayList<>();

    Boolean ansValue = false;

    // iterate through the map of all the answer
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String key = entry.getKey();
      if (key.startsWith("ans")) {
        if (key.endsWith("Value")) {
          try {
            ansValue = Boolean.parseBoolean(entry.getValue());
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
        }
      }

    }
    return answers;
  }

  private User getUserRequesting(HttpServletRequest request) {
    Principal userPrincipal = request.getUserPrincipal();
    String requestingUserEmail = userPrincipal.getName();
    User requestingUser = userRepo.findByEmail(requestingUserEmail);
    return requestingUser;
  }

  private void checkIfNullCaseThrowNotFoundEx(Quest question, String findId) {
    if (question == null)
      throw new ResourceNotFoundException(
          "Not found question with ID: " + findId);
  }

  private Long getLongTypeId(String stringId) {
    Long longId = null;
    try {
      longId = Long.parseLong(stringId);

    } catch (NumberFormatException e) {
      log.error("The string ID cannot parse to Long type!");
    }
    return longId;
  }

  private Boolean checkIfAnyAnswersChanged(
      List<Answer> requestAnswerList,
      List<Answer> existedAnswerList) {
    Boolean wasChanged = false;
    if (requestAnswerList.size() != existedAnswerList.size()) {
      wasChanged = true;
    } else {
      for (int i = 0; i < requestAnswerList.size(); i++) {
        Answer requestAns = requestAnswerList.get(i);
        Answer existedAns = existedAnswerList.get(i);
        if (!requestAns.getText().equals(existedAns.getText())
            || requestAns.getIsCorrect() != existedAns.getIsCorrect()) {
          wasChanged = true;
          break;
        }
      }
    }
    return wasChanged;
  }

}
