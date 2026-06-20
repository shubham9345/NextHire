package com.UserService.UserService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileFilterRequest {
    private Integer yearsOfExperience;
    private String location;
    private String headline;
    private String skills;
    private int page = 0;
    private int size = 10;
}