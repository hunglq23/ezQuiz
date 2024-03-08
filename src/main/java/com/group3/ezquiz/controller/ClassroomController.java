package com.group3.ezquiz.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.payload.CodeFormDto;
import com.group3.ezquiz.service.IClassroomService;
import com.group3.ezquiz.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/classroom")
public class ClassroomController {

    private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";
    private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";

    private final IClassroomService classroomService;

    private final IUserService userService;

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/created-list")
    public String getCreatedClassrooms(HttpServletRequest request, Model model) {
        List<Classroom> classrooms = classroomService.getCreatedClassrooms(request);
        model.addAttribute("classrooms", classrooms);
        return "classroom/lib-classroom";
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @PostMapping("/create")
    public ResponseEntity<?> ClassCreating(
            HttpServletRequest request,
            @Valid ClassroomDto classroomDto) throws BindException {

        return classroomService.createClass(request, classroomDto); // den dia chi
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/{id}")
    public String getClassDetail(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            String code, Model model) {
        Classroom classroom = classroomService.getClassroomByRequestAndId(request, id);
        model.addAttribute("classroom", classroom);

        return "classroom/classroom-update";
    }

    @PostMapping("/{id}/update")
    public String ClassroomUpdating(
            @PathVariable(value = "id") Long id,
            @ModelAttribute("classroom") Classroom updatedClassroom,
            RedirectAttributes redirectAttributes) {
        classroomService.updateClassroom(id, updatedClassroom);
        redirectAttributes.addFlashAttribute("successMessage", "Classroom updated successfully!");
        return "redirect:/classroom/" + id;

    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/{id}/delete")
    public String ClassDeleting(
            @PathVariable(value = "id") Long id) {
        classroomService.deleteClassById(id);
        return "redirect:/classroom/created-list";
    }

    @PreAuthorize(LEARNER_AUTHORITY)
    @GetMapping("/joined-list")
    public String joinClassroomForm(HttpServletRequest request, Model model) {
        model.addAttribute("classroom", new CodeFormDto());
        model.addAttribute("classrooms", userService.getUserRequesting(request).getClassrooms());
        return "classroom/joined-list";
    }

    @PreAuthorize(LEARNER_AUTHORITY)
    @PostMapping("/join")
    public String joinClassroom(
            HttpServletRequest request,
            @RequestParam String code, Model model) {
        boolean success = classroomService.joinClassroom(request, code);
        if (success) {
            model.addAttribute("classrooms", userService.getUserRequesting(request).getClassrooms());
            return "classroom/joined-list";
        }
        return "error";
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/{id}/remove-member")
    public String removeMemberByTeacher(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            @RequestParam(required = true) Long memberId,
            Model model) {
                Classroom classroom = classroomService.getClassroomByRequestAndId(request, id);
                model.addAttribute("classroom", classroomService.removeMemberFromClassroomByMemberId(classroom,memberId));
        return "classroom/classroom-update";
    }
  

}
//