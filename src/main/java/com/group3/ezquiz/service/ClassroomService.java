package com.group3.ezquiz.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.ClassroomDto;
import jakarta.servlet.http.HttpServletRequest;

public interface ClassroomService {
    void createClass(HttpServletRequest request, ClassroomDto classroomDto);

    Optional<Classroom> getClassroomByRequestAndId(HttpServletRequest request, Long id);

    Classroom updateClassroom(Long id, Classroom updatedClassroom);

    void deleteClassroomById(Long id);

    Page<Classroom> getCreatedClassListByPageAndSearchName(HttpServletRequest request, Integer page, String searchName);

    Classroom findByCode(String code);

    void saveClass(Classroom classroom);

    boolean joinClassroom(HttpServletRequest request, String code);

    User getUserRequesting(HttpServletRequest request);

    void removeMemberFromClassroom(Long classId, Long memberId, User requestingUser);

   List <Classroom> getCreatedClassrooms(HttpServletRequest request);

Classroom removeMemberFromClassroomByMemberId(Classroom classroom, Long memberId);

    

}
