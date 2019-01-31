package com.emc.ideaforce.repository;

import com.emc.ideaforce.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {

    List<Story> findByApprovedIsFalse();

    List<Story> findByUserIdEqualsAndApprovedIsTrue(String userId);

    Story findStoryByIdEquals(String entryId);

    List<Story> findByUserIdEquals(String userId);

    List<Story> findTop20ByOrderByLastUpdatedDesc();

    @Query(value = "SELECT DISTINCT e.userId as userId, COUNT(e.userId) as count "
            + "FROM Story e "
            + "GROUP BY e.userId "
            + "ORDER BY COUNT(e.userId) DESC")
    List<ChallengerCountProjection> findUsersByChallengeIdNumberOfChallengesTaken();

}
