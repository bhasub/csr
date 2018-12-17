package com.emc.ideaforce.repository;

import com.emc.ideaforce.model.StoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryImageRepository extends JpaRepository<StoryImage, String> {
}
