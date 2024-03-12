package com.group3.ezquiz.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.ClassroomDto;
import jakarta.servlet.http.HttpServletRequest;

public interface IClassroomService {

    List<Classroom> getCreatedClassrooms(HttpServletRequest request);

    ResponseEntity<?> createClass(HttpServletRequest request, ClassroomDto classroomDto);

    Classroom getClassroomByRequestAndId(HttpServletRequest request, Long id);

    void deleteClassById(Long id);

    Classroom updateClassroom(Long id, Classroom updatedClassroom);

    Classroom findByCode(String code);

    boolean joinClassroom(HttpServletRequest request, String code);

    Classroom removeMemberFromClassroomByMemberId(Classroom classroom, Long memberId);

    boolean importLearnerDataFromExcel( MultipartFile excelFile, Classroom classroom);

    void importClassroomDataFromExcel(HttpServletRequest request, MultipartFile excelFile);
}
