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
    private String id;
    private String type;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isDraft;
    private Integer itemNumber;
    private Timestamp timestamp;

    public String getTimeString() {
        return MyUtils.calculateTimeElapsed(timestamp);
    }
}