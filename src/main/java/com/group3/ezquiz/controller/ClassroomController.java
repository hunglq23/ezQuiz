package com.group3.ezquiz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.payload.CodeFormDto;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.ClassroomService;
import com.group3.ezquiz.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/classrooms/created")
    public String getCreatedClassrooms(Model model,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "") String name) {
        Page<Classroom> classrooms = classroomService.getClassListByPageAndSearchName(page, name);
        model.addAttribute("classrooms", classrooms.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", classrooms.getTotalPages());
        model.addAttribute("search", name);
        model.addAttribute("classroom", new ClassroomDto());
        return "classroom/class-list";
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/classrooms/created")
    public String ClassCreating(HttpServletRequest hRequest,
            @ModelAttribute("classroom") ClassroomDto classroomDto) {
        classroomService.createClass(hRequest, classroomDto);
        return "redirect:/classrooms/created"; // den dia chi
    }

    @GetMapping("/classroom/update/{id}")
    public String getClassroomDetailandUpdate(@PathVariable(value = "id") Long id, Model model) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not be found id " + id));
        ;
        model.addAttribute(("classroom"), classroom);
        return "classroom/classroom-update";
    }

    @PostMapping("/classroom/update/{id}")
    public String ClassroomUpdating(@PathVariable(value = "id") Long id,
            @ModelAttribute("classroom") Classroom updatedClassroom) {
        classroomService.updateClassroom(id, updatedClassroom);
        return "classroom/classroom-update";

    }

    @GetMapping("/classroom/delete/{id}")
    public String ClassroomDeleting(@PathVariable(value = "id") Long id) {
        classroomService.deleteClassroomById(id);
        return "redirect:/classrooms/created";
    }

    @PreAuthorize("hasRole('ROLE_LEARNER')")
    @GetMapping("/classroom/joined-list")
    public String joinClassroomForm(Model model) {
        model.addAttribute("classroom", new CodeFormDto());
        return "classroom/joined-list";
    }

    @PreAuthorize("hasRole('ROLE_LEARNER')")
    @PostMapping("/classroom/join")
    public String joinClassroomForm(HttpServletRequest request,
            @RequestParam String code, Model model) {
        boolean success = classroomService.joinClassroom(request, code);
        if (success) {
            model.addAttribute("classrooms", userService.getUserRequesting(request).getClassrooms());
            return "classroom/joined-list";
        }
        return "error";
    }
}

