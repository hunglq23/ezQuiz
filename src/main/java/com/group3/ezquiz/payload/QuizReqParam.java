package com.group3.ezquiz.payload;

import java.util.HashMap;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizReqParam extends LibraryReqParam {

  private Boolean draft;

  @Override
  public Sort getSortType() {
    switch (super.getSort()) {

      case "AtoZ":
        return Sort.by(Direction.ASC, "title");

      case "ZtoA":
        return Sort.by(Direction.DESC, "title");

      case "oldest":
        return Sort.by(Direction.ASC, "createdAt");

      default:
      case "latest":
        return Sort.by(Direction.DESC, "createdAt");
    }
  }

  @Override
  public HashMap<String, String> getAttrMap() {
    HashMap<String, String> attrMap = super.getAttrMap();
    if (draft != null) {
      attrMap.put("draft", draft.toString());
    }
    return attrMap;
  }

  public QuizReqParam() {
    this(null, null, null, null, null);
  }

  public QuizReqParam(String search, String sort, Integer page, Integer size) {
    this(search, sort, page, size, null);
  }

  public QuizReqParam(String search, String sort, Integer page, Integer size, Boolean draft) {
    super(search, sort, page, size);
    this.draft = draft;
  }

  // constructor from base class with parameter LibraryReqParam
  public QuizReqParam(LibraryReqParam param) {
    super(param.getSearch(), param.getSort(), param.getPage(), param.getSize());
  }

}
