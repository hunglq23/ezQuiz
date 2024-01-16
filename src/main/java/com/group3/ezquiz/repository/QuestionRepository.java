package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.ezquiz.model.Question;

import jakarta.transaction.Transactional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Override
    @Transactional
    <S extends Question> S save(S entity);
}
