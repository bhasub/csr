package com.emc.ideaforce.service;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.ChallengeEntry;
import com.emc.ideaforce.repository.ChallengeDetailRepository;
import com.emc.ideaforce.repository.ChallengeEntryRepository;
import com.emc.ideaforce.repository.ChallengeImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final ChallengeDetailRepository challengeDetailRepository;

    private final ChallengeEntryRepository challengeEntryRepository;

    private final ChallengeImageRepository challengeImageRepository;

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
    public List<ChallengeEntry> getChallengesTakenList(String userId) {
        return emptyList();
    }

    @PostConstruct
    public void run() {
        for (int i = 1; i <= 10; i++) {
            challengeDetailRepository
                    .save(new ChallengeDetail(i + "", "CSR Challenge " + i, "About Challenge " + i + "..."));
        }
    }

    public List<ChallengeEntry> findAllByApprovedIsFalse() {
        return challengeEntryRepository.findAllByApprovedIsFalse();
    }
}
