package com.group3.ezquiz.payload;

import lombok.*;

import java.sql.Timestamp;


public class QuizRequest {
    private String code;
    private String title;
    private String description;
    private boolean is_active;
    private boolean is_exam_only;
    private Integer created_by;
    private Timestamp created_at;

    public QuizRequest() {
    }

    public QuizRequest(String code, String title, String description, boolean is_active, boolean is_exam_only, Integer created_by, Timestamp created_at) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.is_active = is_active;
        this.is_exam_only = is_exam_only;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIs_active() {
        return is_active;
    }

    public boolean getIs_exam_only() {
        return is_exam_only;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public void setIs_exam_only(boolean is_exam_only) {
        this.is_exam_only = is_exam_only;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
