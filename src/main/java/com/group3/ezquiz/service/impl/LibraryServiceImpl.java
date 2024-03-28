package com.group3.ezquiz.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.ClassJoining;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.QuizAssigning;
import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.LibraryResponse;
import com.group3.ezquiz.payload.ObjectDto;
import com.group3.ezquiz.payload.QuizReqParam;
import com.group3.ezquiz.payload.classroom.ClassroomDto;
import com.group3.ezquiz.payload.quiz.QuizAssignedDto;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.service.IClassroomService;
import com.group3.ezquiz.service.ILibraryService;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements ILibraryService {

  private final IQuizService quizService;
  private final IClassroomService classroomService;
  private final IUserService userService;

  @Override
  public LibraryResponse getCreatedQuizAndClassroom(
      HttpServletRequest request,
      LibraryReqParam params) {

    Page<QuizDto> quizPage = quizService.getCreatedQuizList(request, new QuizReqParam(params.getCustom()));

    Page<ClassroomDto> classroomPage = classroomService
        .getCreatedClassrooms(request, params.getCustom());

    double totalEleNumber = classroomPage.getTotalElements() + quizPage.getTotalElements();
    int maxPage = (int) Math.ceil(totalEleNumber / params.getSize());
    LibraryResponse response = LibraryResponse.builder()
        .totalPages(maxPage)
        .exceedMaxPage(false)
        .totalElements(classroomPage.getTotalElements() + quizPage.getTotalElements())
        .build();
    if (params.getPage() > maxPage) {
      response.setExceedMaxPage(true);
    } else {
      response.setObjectDtoList(findContent(quizPage, classroomPage, params));
    }
    return response;
  }

  @Override
  public Page<QuizDto> getMyQuizInLibrary(
      HttpServletRequest request, @Valid QuizReqParam params) {

    return quizService.getCreatedQuizList(request, params);
  }

  @Override
  public Page<ClassroomDto> getMyClassroomInLibrary(
      HttpServletRequest request, @Valid LibraryReqParam params) {

    return classroomService.getCreatedClassrooms(request, params);
  }

  @Override
  public Page<ClassroomDto> getJoinedClassrooms(HttpServletRequest request, @Valid LibraryReqParam params) {

    return classroomService.getJoinedClassrooms(request, params);
  }

  @Override
  public LibraryResponse getQuizAssigned(HttpServletRequest request, LibraryReqParam params) {

    List<ClassJoining> classJoinings = userService.getUserRequesting(request).getClassJoinings();
    List<Classroom> classrooms = classJoinings.stream().map(ClassJoining::getClassroom).collect(Collectors.toList());
    List<QuizAssigning> quizAssignings = new ArrayList<>();
    for (Classroom classroom : classrooms) {
      quizAssignings.addAll(classroom.getAssignedQuizList());
    }

    double totalEleNumber = quizAssignings.size();
    int maxPage = (int) Math.ceil(totalEleNumber / params.getSize());
    LibraryResponse response = LibraryResponse.builder()
        .totalPages(maxPage)
        .exceedMaxPage(false)
        .totalElements(Long.valueOf(quizAssignings.size()))
        .build();
    if (params.getPage() > maxPage) {
      response.setExceedMaxPage(true);
    } else {
      response.setContent(quizAssignings.stream()
          .map(this::mapToQuizAssignedDto)
          .collect(Collectors.toList()));
    }
    return response;
  }

  private QuizAssignedDto mapToQuizAssignedDto(QuizAssigning quizAssigning) {
    return QuizAssignedDto.builder()
        .startDate(quizAssigning.getStartDate())
        .dueDate(quizAssigning.getDueDate())
        .note(quizAssigning.getNote())
        .maxAttempt(quizAssigning.getMaxAttempt())
        .durationInMins(quizAssigning.getDurationInMins())
        .shuffleQuestions(quizAssigning.getQuestionShuffled())
        .shuffleAnswers(quizAssigning.getAnswerShuffled())
        .selectedClassroom(quizAssigning.getClassroom().getId())
        .quiz(quizAssigning.getQuiz())
        .build();
  }

  private List<ObjectDto> findContent(
      Page<QuizDto> quizPage,
      Page<ClassroomDto> classroomPage,
      LibraryReqParam params) {
    if (quizPage.getTotalElements() + classroomPage.getTotalElements() == 0) {
      return null;
    }
    List<ObjectDto> quizList = quizPage.stream().map(this::convertFromQuiz).collect(Collectors.toList());
    List<ObjectDto> classroomList = classroomPage.stream().map(this::converFromClassroom).collect(Collectors.toList());
    List<ObjectDto> objects = new ArrayList<>();
    int index = 0;
    int quizIndex = 0;
    int classroomIndex = 0;
    int end = Math.min(params.getEndIndex(), quizList.size() + classroomList.size());

    while (index < end) {
      ObjectDto toAddObject = null;
      // while there are still elements in both lists
      if (quizIndex < quizList.size() && classroomIndex < classroomList.size()) {
        toAddObject = params.compare(quizList.get(quizIndex), classroomList.get(classroomIndex));
        if (toAddObject.getType().equals("Quiz")) {
          quizIndex++;
        } else {
          classroomIndex++;
        }
      } else if (quizIndex < quizList.size()) { // only quiz list has elements
        toAddObject = quizList.get(quizIndex++);
      } else if (classroomIndex < classroomList.size()) { // only classroom
        toAddObject = classroomList.get(classroomIndex++);
      }
      if (index++ >= params.getStartIndex()) {
        objects.add(toAddObject);
      }
    }
    return objects;
  }

  private ObjectDto convertFromQuiz(QuizDto quizDto) {
    return ObjectDto.builder()
        .id(quizDto.getId().toString())
        .type(quizDto.getType())
        .name(quizDto.getTitle())
        .description(quizDto.getDescription())
        .imageUrl(quizDto.getImageUrl())
        .isDraft(quizDto.getIsDraft())
        .itemNumber(quizDto.getItemNumber())
        .timestamp(quizDto.getTimestamp())
        .build();
  }

  private ObjectDto converFromClassroom(ClassroomDto classroomDto) {
    return ObjectDto.builder()
        .id(classroomDto.getId().toString())
        .type(classroomDto.getType())
        .name(classroomDto.getName())
        .description(classroomDto.getDescription())
        .imageUrl(classroomDto.getImageUrl())
        .itemNumber(classroomDto.getItemNumber())
        .timestamp(classroomDto.getTimestamp())
        .build();
  }

}
