package com.emc.ideaforce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "story_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 100)
    private String imageId;

    @ManyToOne
    @JoinColumn(name = "id")
    private Story story;

    private byte[] data;
}
