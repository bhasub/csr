package com.emc.ideaforce.repository;

import com.emc.ideaforce.model.StoryComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryCommentRepository extends JpaRepository<StoryComments, String> {
    List<StoryComments> findAllByStoryIdEqualsOrderByCreatedDesc(String storyId);
}
