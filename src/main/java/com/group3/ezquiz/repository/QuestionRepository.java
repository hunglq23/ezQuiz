package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.ezquiz.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

}
