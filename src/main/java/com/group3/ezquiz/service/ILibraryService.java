package com.group3.ezquiz.service;

import org.springframework.data.domain.Page;

import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.LibraryResponse;
import com.group3.ezquiz.payload.QuizReqParam;
import com.group3.ezquiz.payload.classroom.ClassroomDto;
import com.group3.ezquiz.payload.quiz.QuizDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface ILibraryService {

  LibraryResponse getCreatedQuizAndClassroom(HttpServletRequest request, LibraryReqParam params);

  Page<QuizDto> getMyQuizInLibrary(HttpServletRequest request, @Valid QuizReqParam params);

  Page<ClassroomDto> getMyClassroomInLibrary(HttpServletRequest request, @Valid LibraryReqParam params);

}
