package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Question;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Long> {

    Question findByText(String text);

}
