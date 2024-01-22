package com.group3.ezquiz.service;

import java.util.List;
import java.util.Optional;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;

import jakarta.servlet.http.HttpServletRequest;

public interface ClassroomService {
    List <Classroom> getAllClass(String keyword);
    void createClass(HttpServletRequest request, ClassroomDto classroomDto);
   Optional <Classroom> getClassroomById(Long id);
    Classroom updateClassroom(Long id, Classroom updatedClassroom);
    void deleteClassroomById(Long id);
}
