package com.group3.ezquiz.payload.quiz;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
public class QuizReqParam {

    private String search = "";

    @Pattern(regexp = "^(oldest|latest)$")
    private String sort = "latest";

    private String draft = "";

    @Min(1)
    private Integer size = 3;

    @Min(1)
    private Integer page = 1;

    public HashMap<String, String> getAttrMap() {

        return new HashMap<>(Map.of(
                "search", this.search,
                "sort", this.sort,
                "draft", this.draft,
                "page", this.page.toString(),
                "size", this.size.toString()));
    }

}
