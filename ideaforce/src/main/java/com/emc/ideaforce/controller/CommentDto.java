package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.User;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CommentDto {
    private String comment;
    private String storyId;
}
