package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Quiz findQuizByQuizId(Integer id);
}
