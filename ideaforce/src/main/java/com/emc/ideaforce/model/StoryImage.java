package com.emc.ideaforce.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "story_images")
public class StoryImage {

    @Id
    @Column(length = 100)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String imageId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "entry_id")
    private Story story;

    @Lob
    private byte[] data;

}
