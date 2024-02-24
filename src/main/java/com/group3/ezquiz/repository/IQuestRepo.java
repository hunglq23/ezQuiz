package com.group3.ezquiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.group3.ezquiz.model.Quest;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.model.Answer;

public interface IQuestRepo extends JpaRepository<Quest, Long> {

  @Query("SELECT q FROM Quest q " +
      "WHERE" + "(q.isPublic = :isPublic)")
  List<Quest> findByIsPublic(Boolean isPublic);

  List<Quest> findByCreator(User creator);

  Quest findByIdAndCreator(Long id, User creator);

  List<Quest> findByAnswersIn(List<Answer> answers);

  Quest findByIdAndAnswersIn(Long id, List<Answer> answers);

  // HI
  @Query("SELECT q FROM Quest q " +
      "LEFT JOIN Answer a " + "ON" + "(q.id = a.question.id) " +
      "WHERE" + "(q.id = :id)" +
      "AND" + "(a.text = :aText)" +
      "AND" + "(a.isCorrect = :aValue)")
  Quest findByIdAndAnsTextAndAnsValue(Long id, String aText, Boolean aValue);

}
