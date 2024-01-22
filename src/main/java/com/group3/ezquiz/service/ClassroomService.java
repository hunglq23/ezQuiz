package com.group3.ezquiz.service;

import java.util.List;
import java.util.Optional;

import com.group3.ezquiz.model.Classroom;

public interface ClassroomService {
    List <Classroom> getAllClass(String keyword);
    void createClass(Classroom classroom);
   Optional <Classroom> getClassroomById(Long id);
    Classroom updateClassroom(Long id, Classroom updatedClassroom);
    void deleteClassroomById(Long id);
}
