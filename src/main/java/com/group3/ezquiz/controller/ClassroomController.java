    package com.group3.ezquiz.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String getClassList(Model model,
    @Param("keyword") String keyword) {
       
        model.addAttribute("listClass", classroomService.getAllClass(keyword));
        model.addAttribute("keyword", keyword);
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
        Principal principal = request.getUserPrincipal(); // chua thong tin user hien tai
        Classroom classroom = Classroom.builder()
                .className(dto.getClassName())
                .description(dto.getDescription())
                .creator(userRepo.findByEmail(principal.getName()))
                .build();
        classroomService.createClass(classroom);
        return "redirect:/teacher/classlist"; // den dia chi
    }

    @GetMapping("/classroom/update/{id}")
    public String getClassroomCreatingForm(@PathVariable(value = "id") Long id, Model model) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not be found id " + id));
        ;
        model.addAttribute(("classroom"), classroom);
        return "/classroom/classroom-updating";
    }

    @PostMapping("/classroom/update/{id}")

    public String ClassroomUpdating(@PathVariable(value = "id") Long id,
            @ModelAttribute("classroom") Classroom updatedClassroom) {
        classroomService.updateClassroom(id, updatedClassroom);
        return "redirect:/teacher/classlist";

    }

    @GetMapping("/classroom/delete/{id}")
    public String ClassroomDeleting(@PathVariable(value = "id") Long id) {
        classroomService.deleteClassroomById(id);
        return "redirect: teacher/classlist";
    }

}
