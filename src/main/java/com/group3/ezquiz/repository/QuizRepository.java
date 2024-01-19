package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.Quiz;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Integer> {

}
