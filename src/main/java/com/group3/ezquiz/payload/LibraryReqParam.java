package com.group3.ezquiz.payload;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryReqParam {

  private final String DEFAULT_SORT = "latest";
  private final int DEFAULT_PAGE = 1;
  private final int DEFAULT_SIZE = 3;

  private String search = "";

  @Pattern(regexp = "^(oldest|latest|A -> Z|Z -> A)$")
  private String sort = "latest";
  @Min(1)
  private Integer size = 3;
  @Min(1)
  private Integer page = 1;

  public HashMap<String, String> getAttrMap() {

    return new HashMap<>(Map.of(
        "search", this.search,
        "sort", this.sort,
        "page", this.page.toString(),
        "size", this.size.toString()));
  }

  public void handleWhenError(BindingResult bindingResult) {
    for (ObjectError objectError : bindingResult.getAllErrors()) {
      FieldError fieldError = (FieldError) objectError;
      if (fieldError.getField().equals("sort")) {
        setSort(DEFAULT_SORT);
      }
      if (fieldError.getField().equals("page")) {
        setPage(DEFAULT_PAGE);
      }
      if (fieldError.getField().equals("size")) {
        setSize(DEFAULT_SIZE);
      }
    }
  }

  public Sort getSortType() {
    switch (sort) {

      case "A -> Z":
        return Sort.by(Direction.ASC, "title");

      case "Z -> A":
        return Sort.by(Direction.DESC, "title");

      case "oldest":
        return Sort.by(Direction.ASC, "createdAt");

      default:
      case "latest":
        return Sort.by(Direction.DESC, "createdAt");
    }
  }

}
