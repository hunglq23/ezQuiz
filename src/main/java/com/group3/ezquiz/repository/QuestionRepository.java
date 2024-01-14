package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Integer> {
}
