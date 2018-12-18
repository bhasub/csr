package com.emc.ideaforce.service;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.repository.ChallengeDetailRepository;
import com.emc.ideaforce.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
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
     * @param id
     */
    public ChallengeDetail getChallengeDetail(String id) {
        return challengeDetailRepository.getOne(id);
    }

    /**
     * Return the challenges taken by the user
     *
     * @param userId
     */
    public List<Story> getStories(String userId) {
        //note: todo by user id
        return storyRepository.findAll();
    }

    @PostConstruct
    public void run() {
        for (int i = 1; i <= 10; i++) {
            challengeDetailRepository
                    .save(new ChallengeDetail(i + "", "CSR Challenge " + i, "About Challenge " + i + "..."));
        }
        storyRepository.deleteAll();
        for (int i = 1; i <= 10; i++) {
            Story storyObj = new Story();

            storyObj.setApproved(false);
            storyObj.setChallengeId(i+"Entry");
            storyObj.setUserId("temp_user");
            storyObj.setCreated(new Date());
            storyObj.setLastUpdated(new Date());
            storyRepository.save(storyObj);
        }
    }

    public List<Story> findAllByApprovedIsFalse() {
        return storyRepository.findAllByApprovedIsFalse();
    }

    public void setStoryApproved(String entryId) {
        Story storyObj = storyRepository.findStoryByIdEquals(entryId);
        storyObj.setApproved(true);
        storyRepository.save(storyObj );
    }
}
