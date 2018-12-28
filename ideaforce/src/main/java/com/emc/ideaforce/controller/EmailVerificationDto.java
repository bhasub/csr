package com.emc.ideaforce.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationDto {

    @NotEmpty
    @ValidEmail
    private String emailId;

    private String verificationCode;

    private boolean codeGenerated;
}
