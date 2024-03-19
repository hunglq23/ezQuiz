package com.group3.ezquiz.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.QuizAssigning;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.QuizAssigningRepository;
import com.group3.ezquiz.service.IClassroomService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizAssigningService {

    private final QuizAssigningRepository assigningQuizRepository;
    private final IClassroomService classroomService;

    public void create(QuizAssigning assigningQuiz) {
        assigningQuizRepository.save(assigningQuiz);
    }

    public List<QuizAssigning> getAssigningQuizByClassroom(Classroom classroom) {
        return assigningQuizRepository.findByClassroom(classroom);
    }

    public List<QuizAssigning> getAssigningQuizsForStudent(User user) {
        return assigningQuizRepository.findByClassroomIn(user.getClassrooms());
    }

    public List<QuizAssigning> getAssigningQuizByTeacherId(Long teacherId) {
        return assigningQuizRepository.findByCreatorId(teacherId);
    }

    public QuizAssigning findById(Long id) {
        return assigningQuizRepository.findById(id).orElse(null);
    }

    public void removeAssignedQuiz(Long id) {
        assigningQuizRepository.deleteById(id);
    }

}
