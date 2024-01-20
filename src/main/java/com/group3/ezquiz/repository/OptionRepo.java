package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.Question;

import jakarta.transaction.Transactional;

@Repository
public interface OptionRepo extends JpaRepository<Option, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Option o WHERE o.question.id = :questionId")
    void deleteAllByQuestion(@Param("questionId") Long questionId);

}
