package com.group3.ezquiz.payload;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
public class ClassroomDto {
    private String className;
    private String description;
    private Long userId;
}
