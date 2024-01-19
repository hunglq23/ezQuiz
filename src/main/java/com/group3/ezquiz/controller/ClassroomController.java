package com.group3.ezquiz.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.ClassroomService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/teacher/classlist")
    public String getClassList(Model model) {
        model.addAttribute("listClass", classroomService.getAllClass());
        return "classlist";
    }

    @GetMapping("/classroom/create")
    public String getClassCreatingForm(Model model) {
        model.addAttribute("classroom", new ClassroomDto());
        return "classroom/classroom-creating"; // den file html
    }

    @PostMapping("/classroom/create")
    public String ClassCreating(HttpServletRequest request,
        @ModelAttribute("classroom") ClassroomDto dto) {
        Principal principal = request.getUserPrincipal();
        Classroom classroom = Classroom.builder()
                .className(dto.getClassName())
                .description(dto.getDescription())
                .creator(userRepo.findByEmail(principal.getName()))
                .build();
        classroomService.createClass(classroom);
        return "redirect:/teacher/classlist"; //den dia chi 
    }

        
}
