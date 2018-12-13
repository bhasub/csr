package com.emc.ideaforce.model;

import com.emc.ideaforce.controller.PasswordMatches;
import com.emc.ideaforce.controller.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@PasswordMatches
public class UserDto {

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 20, message="Password should be 8-20 chars")
    private String password;
    private String matchingPassword;
    private String[] roles;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;

}
