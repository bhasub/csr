package com.emc.ideaforce.controller;

import lombok.Data;

@Data
public class CommentDto {
    private int id;
    private String comment;
    private String storyId;
}
