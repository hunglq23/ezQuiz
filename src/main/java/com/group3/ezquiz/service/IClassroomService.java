package com.group3.ezquiz.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IClassroomService {

    List<Classroom> getCreatedClassroomList(HttpServletRequest request);

    ResponseEntity<?> createClass(HttpServletRequest request, @Valid ClassroomDto classroomDto);

    void importClassroomDataFromExcel(HttpServletRequest request, MultipartFile excelFile);

    Classroom getClassroomByRequestAndId(HttpServletRequest request, Long id);

    boolean importLearnerDataFromExcel( MultipartFile multipartFile, Classroom classroom);

    Classroom updateClassroom(Long id, Classroom updatedClassroom);

    void deleteClassById(Long id);

    boolean joinClassroom(HttpServletRequest request, String code);

    
}
