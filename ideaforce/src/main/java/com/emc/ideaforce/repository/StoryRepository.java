package com.emc.ideaforce.repository;

import com.emc.ideaforce.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {

    List<Story> findAllByApprovedIsFalse();

    Story findStoryByIdEquals(String entryId);

}
