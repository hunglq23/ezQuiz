package com.group3.ezquiz.payload;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LibraryResponse {
    private Integer totalPages;
    private List<ObjectDto> objectDtoList;
    private Boolean exceedMaxPage;
    private Long totalElements;
}
