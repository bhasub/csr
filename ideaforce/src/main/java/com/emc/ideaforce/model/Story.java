package com.emc.ideaforce.model;

import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.NONE;

@Data
@Entity
@Table(name = "stories")
public class Story {

    @Id
    @Column(length = 100)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    /**
     * Reference to global challenge id
     */
    private String challengeId;

    private String userId;

    private String title;

    private String description;

    private String video;

    @Setter(NONE)
    @OneToMany(mappedBy = "story", cascade = ALL, orphanRemoval = true)
    private List<StoryImage> images = new ArrayList<>();

    private boolean approved;

    private Date created;

    private Date lastUpdated;

    public Story() {
        this.created = new Date();
        this.lastUpdated = new Date();
    }

    public void addImage(StoryImage image) {
        this.images.add(image);
        image.setStory(this);
    }

}
