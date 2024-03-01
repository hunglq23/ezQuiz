package com.group3.ezquiz.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.payload.CodeFormDto;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.ClassroomService;
import com.group3.ezquiz.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    // @PreAuthorize("hasRole('ROLE_TEACHER')")
    // @GetMapping("/classrooms/created")
    // public String getCreatedClassrooms(HttpServletRequest request, Model model,
    // @RequestParam(defaultValue = "0") Integer page,
    // @RequestParam(required = false, defaultValue = "") String name) {
    // Page<Classroom> classrooms =
    // classroomService.getCreatedClassListByPageAndSearchName(request, page, name);

    // model.addAttribute("classrooms", classrooms.getContent());
    // model.addAttribute("currentPage", page);
    // model.addAttribute("totalPages", classrooms.getTotalPages());
    // model.addAttribute("search", name);
    // model.addAttribute("classroom", new ClassroomDto());
    // return "classroom/class-list";
    // }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/classrooms/created-list")
    public String getCreatedClassrooms(HttpServletRequest request, Model model) {
        List<Classroom> classrooms = classroomService.getCreatedClassrooms(request);
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("classroom", new ClassroomDto());
        return "classroom/created-list";
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/classrooms/create")
    public String ClassCreating(HttpServletRequest hRequest,
            @ModelAttribute("classroom") ClassroomDto classroomDto) {
        classroomService.createClass(hRequest, classroomDto);
        return "redirect:/classrooms/created-list"; // den dia chi
    }

    @GetMapping("/classroom/{id}")
    public String getClassroomDetail(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            String code, Model model) {
        Classroom classroom = classroomService.getClassroomByRequestAndId(request, id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not be found id " + id));
        ;

        model.addAttribute(("classroom"), classroom);
        return "classroom/classroom-update";
    }

    @PostMapping("/classroom/update/{id}")
    public String ClassroomUpdating(
            @PathVariable(value = "id") Long id,
            @ModelAttribute("classroom") Classroom updatedClassroom,
            RedirectAttributes redirectAttributes) {
        classroomService.updateClassroom(id, updatedClassroom);
        redirectAttributes.addFlashAttribute("successMessage", "Classroom updated successfully!");
        return "redirect:/classroom/" + id;

    }

    @GetMapping("/classroom/delete/{id}")
    public String ClassroomDeleting(@PathVariable(value = "id") Long id) {
        classroomService.deleteClassroomById(id);
        return "redirect:/classrooms/created-list";
    }

    @PreAuthorize("hasRole('ROLE_LEARNER')")
    @GetMapping("/classroom/joined-list")
    public String joinClassroomForm(HttpServletRequest request, Model model) {
        model.addAttribute("classroom", new CodeFormDto());
        model.addAttribute("classrooms", userService.getUserRequesting(request).getClassrooms());
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

    @GetMapping("/classroom/{id}/remove-member")
    public String removeMemberByTeacher(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            @RequestParam(required = true) Long memberId, Model model) {
        Classroom classroom = classroomService.getClassroomByRequestAndId(request, id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not be found id " + id));
        model.addAttribute("classroom", classroomService.removeMemberFromClassroomByMemberId(classroom, memberId));
        return "classroom/classroom-update";
    }

}
