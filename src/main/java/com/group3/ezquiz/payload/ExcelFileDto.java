package com.group3.ezquiz.payload;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelFileDto {
    @NotNull(message = "file null")
    private MultipartFile excelFile;
}
