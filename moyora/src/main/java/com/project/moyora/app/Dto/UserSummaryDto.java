package com.project.moyora.app.dto;

import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryDto {
    private Long id;
    private String name;
    private Integer age;
    private GenderType gender;

    public UserSummaryDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.age = user.getAge();
        this.gender = user.getGender();
    }

}
