    package com.group3.ezquiz.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private UserRepo userRepo;
     
   public String viewClassList(Model model){
    String keyword = "";
    return getClassList(model, 1, keyword);
   }
    
    @GetMapping("/teacher/classlist/{pageNumber}")
    public String getClassList(Model model,
    @PathVariable("pageNumber") int currentPage,
    @Param("keyword") String keyword) {
       Page<Classroom> page = classroomService.getAllClass(currentPage, keyword);
       long totalItems = page.getTotalElements();
       int totalPages = page.getTotalPages();
       model.addAttribute("currentPage", currentPage);
       model.addAttribute("totalItems", totalItems);
       model.addAttribute("totalPages", totalPages);
        model.addAttribute("listClass", page);
        model.addAttribute("keyword", keyword);
        return "classlist";
    }

    @GetMapping("/classroom/create")
    public String getClassCreatingForm(Model model) {
        model.addAttribute("classroom", new ClassroomDto());
        return "classroom/classroom-creating"; // den file html
    }

    @PostMapping("/classroom/create")
    public String ClassCreating(HttpServletRequest hRequest,
            @ModelAttribute("classroom") ClassroomDto classroomDto) {
        classroomService.createClass(hRequest, classroomDto);
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
        return "redirect:/teacher/classlist";
    }

}
