package com.group3.ezquiz.payload;

import java.util.List;

import com.group3.ezquiz.model.Option;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String text;
    private List<Option> options;
}
