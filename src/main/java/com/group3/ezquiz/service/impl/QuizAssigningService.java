package com.group3.ezquiz.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private final QuizAssigningRepository assigningQuizRepository;
    private final IClassroomService classroomService;

    public void create(QuizAssigning assigningQuiz) {
        assigningQuizRepository.save(assigningQuiz);
    }

    public List<QuizAssigning> getAssignedQuizzesForLearner(Long learnerId) {
        return assigningQuizRepository.findByClassroomClassJoiningsLearnerId(learnerId);
    }

    public List<QuizAssigning> getAssignedQuizForTeacher(Long teacherId) {
        List<QuizAssigning> quizAssignings = new ArrayList<>();
        List<Classroom> classrooms = classroomService.findClassroomsByCreatorId(teacherId);

        for (Classroom classroom : classrooms) {
            List<QuizAssigning> quizAssigningsForClassroom = assigningQuizRepository
                    .findByClassroomId(classroom.getId());
            quizAssignings.addAll(quizAssigningsForClassroom);

        }

        return quizAssignings;
    }

    public QuizAssigning findById(Long id) {
        return assigningQuizRepository.findById(id).orElse(null);
    }

    public void removeAssignedQuiz(Long id) {
        assigningQuizRepository.deleteById(id);
    }

}
