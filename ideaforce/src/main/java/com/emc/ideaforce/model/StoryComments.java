package com.emc.ideaforce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.Date;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name = "story_comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoryComments {
    @Id
    @Column(length = 100)
    @GeneratedValue(strategy = AUTO)
    private int id;

    @ManyToOne(fetch= FetchType.EAGER)
    private User user;

    private String comment;

    private String storyId;

    private Date created;

    private Date lastUpdated;
}
