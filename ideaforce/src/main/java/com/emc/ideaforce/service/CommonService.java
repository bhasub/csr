package com.emc.ideaforce.service;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.repository.ChallengeDetailRepository;
import com.emc.ideaforce.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final ChallengeDetailRepository challengeDetailRepository;

    private final StoryRepository storyRepository;

    /**
     * Returns the global Challenges list
     */
    public List<ChallengeDetail> getChallengeDetailList() {
        return challengeDetailRepository.findAll();
    }

    /**
     * Return the challenge detail
     *
     * @param id unique identifier for the challenge
     */
    public ChallengeDetail getChallengeDetail(String id) {
        return challengeDetailRepository.getOne(id);
    }

    /**
     * Return the challenges taken by the user
     *
     * @param userId unique identifier for the user
     */
    public List<Story> getStories(String userId) {
        return storyRepository.findAll();   // TODO: Get stories by user id
    }

    /**
     * Submit story taken by the user
     *
     * @param story story taken by user
     */
    public void submitStory(Story story) {
        storyRepository.save(story);
    }

    public List<Story> findAllByApprovedIsFalse() {
        return storyRepository.findAllByApprovedIsFalse();
    }

    public void setStoryApproved(String entryId) {
        Story story = storyRepository.findStoryByIdEquals(entryId);
        story.setApproved(true);
        storyRepository.save(story);
    }

    @PostConstruct
    public void run() {
        for (int i = 1; i <= 10; i++) {
            challengeDetailRepository
                    .save(new ChallengeDetail(i + "", "CSR Challenge " + i, "About Challenge " + i + "..."));
        }

        storyRepository.deleteAll();

        for (int i = 1; i <= 10; i++) {
            Story story = new Story();

            story.setChallengeId(i + " entry");
            story.setUserId("temp_user");
            storyRepository.save(story);
        }
    }

}
