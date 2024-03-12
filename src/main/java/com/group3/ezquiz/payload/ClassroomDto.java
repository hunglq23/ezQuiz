package com.group3.ezquiz.payload;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassroomDto {

    @Size(min = 3, message = "Name at lease 3 characters!")
    private String name;
    private String description;

    public void setName(final String name) {
        this.name = name.trim().replaceAll("\\s+", " ");
    }

    public void setDescription(String description) {
        this.description = description.trim().isEmpty() ? null : description;
    }
 
    
    
}
