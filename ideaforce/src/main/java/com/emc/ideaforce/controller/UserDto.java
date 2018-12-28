package com.emc.ideaforce.controller;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@PasswordMatches
public class UserDto {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String employeeId;

    @NotEmpty
    @ValidEmail
    private String email;

    @NotEmpty
    @Size(min = 8, max = 20, message = "Password should be 8-20 chars")
    private String password;
    private String matchingPassword;

}
