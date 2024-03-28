package com.group3.ezquiz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.classroom.ClassroomDetailDto;
import com.group3.ezquiz.payload.classroom.ClassroomDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IClassroomService {

    ResponseEntity<?> createClass(HttpServletRequest request, @Valid ClassroomDetailDto classroomDto);

    void importClassroomDataFromExcel(HttpServletRequest request, MultipartFile excelFile);

    Classroom getClassroomByRequestAndId(HttpServletRequest request, Long id);

    boolean importLearnerDataFromExcel(MultipartFile multipartFile, Classroom classroom);

    Classroom updateClassroom(Long id, Classroom updatedClassroom);

    void deleteClassById(Long id);

    boolean joinClassroom(HttpServletRequest request, String code);

    Page<ClassroomDto> getCreatedClassrooms(HttpServletRequest request, LibraryReqParam libraryDto);

    List<Classroom> findClassroomsByCreatorId(Long creatorId);

    Page<ClassroomDto> getJoinedClassrooms(HttpServletRequest request, @Valid LibraryReqParam params);

    void removeLearnerFromClassroomByClassJoiningId(Classroom classroom, Long classJoiningId);
}
