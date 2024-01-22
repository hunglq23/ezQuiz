package com.group3.ezquiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Question;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Long> {

    Question findByText(String text);

    Question findQuestionByQuestionId(Long questionId);

    List<Question> findByTextContainingIgnoreCase(String searchText);

}
