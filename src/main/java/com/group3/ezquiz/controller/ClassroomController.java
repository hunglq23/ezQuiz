package com.group3.ezquiz.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.service.IClassroomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/classroom")
public class ClassroomController {

    private final IClassroomService classroomService;

    private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";
    private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/created-list")
    public String getCreatedClassroomPage(HttpServletRequest request, Model model) {
        List<Classroom> classrooms = classroomService.getCreatedClassroomList(request);
        model.addAttribute("classrooms", classrooms);
        return "classroom/classroom-list";
    }

   @PreAuthorize(TEACHER_AUTHORITY)
    @PostMapping("/create")
    public ResponseEntity<?> ClassCreating(
            HttpServletRequest request,
            @Valid ClassroomDto classroomDto) throws BindException {

        return classroomService.createClass(request, classroomDto); 
    }
    




}
