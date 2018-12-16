package com.emc.ideaforce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "stories")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 100)
    private String id;

    /**
     * Reference to global challenge id
     */
    private String challengeId;

    private String userId;

    private String[] videos;

    @OneToMany(targetEntity = StoryImage.class, mappedBy = "imageId", fetch = FetchType.LAZY)
    private List<StoryImage> images;

    private boolean approved;

    private Date created;

    private Date lastUpdated;

}
