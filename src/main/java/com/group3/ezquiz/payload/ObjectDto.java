package com.group3.ezquiz.payload;

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
    private String timeString;
}
