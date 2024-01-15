package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.Quiz;
import org.springframework.data.repository.CrudRepository;

public interface QuizRepository extends CrudRepository<Quiz, Integer> {
}
