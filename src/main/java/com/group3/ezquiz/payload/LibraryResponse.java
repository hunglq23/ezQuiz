package com.group3.ezquiz.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryResponse {
    private Integer maxPage;
    private List<ObjectDto> objectDtoList;
    private Boolean exceedMaxPage = true;

    public LibraryResponse(Integer maxPage) {
        this.maxPage = maxPage;
    }

}
