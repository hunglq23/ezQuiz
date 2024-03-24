package com.group3.ezquiz.payload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizReqParam extends LibraryReqParam {

  private Boolean draft;

}
