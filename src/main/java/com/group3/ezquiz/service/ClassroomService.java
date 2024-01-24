package com.group3.ezquiz.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;

import jakarta.servlet.http.HttpServletRequest;

public interface ClassroomService {
    Page <Classroom> getAllClass(int pageNumber, String keyword);
    void createClass(HttpServletRequest request, ClassroomDto classroomDto);
   Optional <Classroom> getClassroomById(Long id);
    Classroom updateClassroom(Long id, Classroom updatedClassroom);
    void deleteClassroomById(Long id);
}
