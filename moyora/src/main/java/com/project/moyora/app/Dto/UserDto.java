package com.project.moyora.app.dto;

import com.project.moyora.app.domain.GenderType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private GenderType gender;
    private LocalDate birth;
    private Boolean verified;
}
