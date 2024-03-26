package com.group3.ezquiz.payload.classroom;

import java.sql.Timestamp;

import com.group3.ezquiz.utils.MyUtils;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassroomDto {
  @Builder.Default
  private String type = "Classroom";
  @Builder.Default
  private Boolean isDraft = false;
  private Long id;
  private String name;
  private String description;
  private String imageUrl;
  private Integer itemNumber;
  private Timestamp timestamp;

  public String getTimeString() {
    return MyUtils.calculateTimeElapsed(timestamp);
  }
}
