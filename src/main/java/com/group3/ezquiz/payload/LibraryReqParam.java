package com.group3.ezquiz.payload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@AllArgsConstructor
public class LibraryReqParam {

  private final String DEFAULT_SORT = "latest";
  private final int DEFAULT_PAGE = 1;
  private final int DEFAULT_SIZE = 3;

  @With
  private String search = "";

  @With
  @NotNull
  @Pattern(regexp = "^(oldest|latest|AtoZ|ZtoA)$")
  private String sort = "latest";
  @NotNull
  @Min(1)
  private Integer page = 1;
  @NotNull
  @Min(1)
  private Integer size = 3;

  public void setSearch(String search) {
    this.search = search.trim().replaceAll("\\s+", " ");
  }

  public String getSearch() {
    return search == null ? "" : search;
  }

  public HashMap<String, String> getAttrMap() {
    HashMap<String, String> attrMap = new HashMap<>();
    if (search != null && !search.isEmpty()) {
      attrMap.put("search", search);
    }
    attrMap.putAll(new HashMap<>(Map.of(
        "sort", this.sort,
        "page", this.page.toString(),
        "size", this.size.toString())));
    return attrMap;
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

      case "AtoZ":
        return Sort.by(Direction.ASC, "name");

      case "ZtoA":
        return Sort.by(Direction.DESC, "name");

      case "oldest":
        return Sort.by(Direction.ASC, "createdAt");

      default:
      case "latest":
        return Sort.by(Direction.DESC, "createdAt");
    }
  }

  public ObjectDto compare(ObjectDto o1, ObjectDto o2) {
    switch (sort) {
      case "AtoZ":
        return o1.getName().compareToIgnoreCase(o2.getName()) < 0 ? o1 : o2;

      case "ZtoA":
        return o1.getName().compareToIgnoreCase(o2.getName()) > 0 ? o1 : o2;

      case "oldest":
        return o1.getTimestamp().compareTo(o2.getTimestamp()) < 0 ? o1 : o2;

      default:
      case "latest":
        return o1.getTimestamp().compareTo(o2.getTimestamp()) > 0 ? o1 : o2;
    }
  }

  public int getStartIndex() {
    return size * (page - 1);
  }

  public int getEndIndex() {
    return size * page;
  }

  public LibraryReqParam getCustom() {
    return new LibraryReqParam(getSearch(), getSort(), 1, getEndIndex());
  }

  public String sort() {
    if (sort.equals("AtoZ")) {
      return "A to Z";
    } else if (sort.equals("ZtoA")) {
      return "Z to A";
    }
    return sort.substring(0, 1).toUpperCase() + sort.substring(1);
  }

  private final String[] COLORS = {
      "#FFC107", "#FF5722", "#4CAF50",
      "#2196F3", "#9C27B0", "#ec3c6c",
      "#04cc84", "#6c6c6c", "#8c54c4" };
  private final List<Integer> colorIndexUsed = new ArrayList<>();

  public String randomBgColor() {

    Integer randomIndex = null;
    do {
      randomIndex = (int) (Math.random() * COLORS.length);
      if (colorIndexUsed.size() == COLORS.length) {
        colorIndexUsed.clear();
      }
    } while (colorIndexUsed.contains(randomIndex));
    colorIndexUsed.add(randomIndex);
    return COLORS[randomIndex];
  }

}
