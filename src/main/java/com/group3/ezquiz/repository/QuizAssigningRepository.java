package com.group3.ezquiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.QuizAssigning;

@Repository
public interface QuizAssigningRepository extends JpaRepository<QuizAssigning, Long> {
    List<QuizAssigning> findByClassroomId(Long classroomId);

    List<QuizAssigning> findByClassroomClassJoiningsLearnerId(Long learnerId);

}
