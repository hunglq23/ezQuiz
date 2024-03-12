package com.group3.ezquiz.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IClassroomService {

    List<Classroom> getCreatedClassroomList(HttpServletRequest request);

    ResponseEntity<?> createClass(HttpServletRequest request, @Valid ClassroomDto classroomDto);
}
