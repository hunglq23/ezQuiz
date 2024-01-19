package com.group3.ezquiz.service;

import java.util.List;

import com.group3.ezquiz.model.Classroom;

public interface ClassroomService {
    List <Classroom> getAllClass();
    void createClass(Classroom classroom);
}
