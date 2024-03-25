package com.group3.ezquiz.payload;

import java.sql.Timestamp;

import com.group3.ezquiz.utils.MyUtils;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ObjectDto {
    private String type;
    private String name;
    private String description;
    private String image;
    private Boolean isDraft;
    private Integer itemNumber;
    private Timestamp timestamp;

    public String getTimeString() {
        return MyUtils.calculateTimeElapsed(timestamp);
    }
}