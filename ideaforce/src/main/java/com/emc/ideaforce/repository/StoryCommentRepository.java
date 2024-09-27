package com.emc.ideaforce.repository;

import com.emc.ideaforce.model.StoryComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryCommentRepository extends JpaRepository<StoryComments, String> {
    StoryComments findByStoryIdEquals(String storyId);
}
