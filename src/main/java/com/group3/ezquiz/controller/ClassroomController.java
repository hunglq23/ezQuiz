package com.group3.ezquiz.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.formula.functions.Mode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.payload.ExcelFileDto;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.service.IClassroomService;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/classroom")
public class ClassroomController {

    private final IClassroomService classroomService;
    private final IUserService userService;

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

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/{id}")
    public String getClassDetail(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            String code, Model model) {
        Classroom classroom = classroomService.getClassroomByRequestAndId(request, id);
        model.addAttribute("classroom", classroom);
        return "classroom/classroom-detail";
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
    public String JoinClassroomForm(
            HttpServletRequest request,
            Model model) {
        model.addAttribute("classrooms", userService.getUserRequesting(request).getClassJoinings());
        return "classroom/classroom-list";
    }

    @PreAuthorize(LEARNER_AUTHORITY)
    @PostMapping("/join")
    public String JoinedClassroom(
            HttpServletRequest request,
            @RequestParam String code, Model model) {
        boolean success = classroomService.joinClassroom(request, code);
        
        return null;
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> getNewClassroomTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/classroom/Template.xlsx");
        InputStream inputStream = resource.getInputStream();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Template.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(inputStream));
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @PostMapping("/import")

    public ResponseEntity<?> importClassroomData(
            HttpServletRequest request,
            @ModelAttribute ExcelFileDto fileDto) throws BindException {
        classroomService.importClassroomDataFromExcel(request, fileDto.getExcelFile());
        return new ResponseEntity<>(
                MessageResponse.builder()
                        .message("Import Successfully")
                        .build(),
                HttpStatus.OK);
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @PostMapping("/{id}/import")
    public ResponseEntity<?> importMemberClassroomData(
            HttpServletRequest request,
            @PathVariable Long id,
            @ModelAttribute ExcelFileDto excelFile,
            Model model) throws BindException {
        Classroom classroom = classroomService.getClassroomByRequestAndId(request, id);
        classroomService.importLearnerDataFromExcel(excelFile.getExcelFile(), classroom);
        return new ResponseEntity<>(
                MessageResponse.builder()
                        .message("Import Successfully")
                        .build(),
                HttpStatus.OK);
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> getNewLeanerTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/classroom/Template.xlsx");
        InputStream inputStream = resource.getInputStream();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Template.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(inputStream));
    }

}
