package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.model.StoryImage;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.utils.CommonException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Common web controller
 */
@Controller
@RequiredArgsConstructor
public class CommonController {

    private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

    private static final String CHALLENGES = "challenges";
    private static final String CHALLENGE_DETAIL = "challengedetail";
    private static final String CHALLENGE = "challenge";
    private static final String USER_CHALLENGES = "userchallenges";

    private static final String HOME_VIEW = "index";
    private static final String SUBMIT_STORY_VIEW = "submitstory";
    private static final String LEADER_BOARD_VIEW = "leaderboard";
    private static final String MESSAGE = "message";

    private final CommonService commonService;

    @GetMapping("/")
    public ModelAndView index(Principal principal) {
        return new ModelAndView(HOME_VIEW);
    }

    @GetMapping("/home")
    public ModelAndView home(Principal principal) {
        return new ModelAndView(HOME_VIEW);
    }

    @GetMapping("/challenges")
    public ModelAndView challengesList(Principal principal) {
        List<ChallengeDetail> challengeDetailList = commonService.getChallengeDetailList();

        ModelAndView mv = new ModelAndView(CHALLENGES);
        mv.addObject(CHALLENGES, challengeDetailList);
        return mv;
    }

    @GetMapping("/challenges/{id}")
    public ModelAndView challengeDetail(@PathVariable String id) {
        ChallengeDetail challengeDetail = commonService.getChallengeDetail(id);

        ModelAndView mv = new ModelAndView(CHALLENGE_DETAIL);
        mv.addObject(CHALLENGE, challengeDetail);
        return mv;
    }

    @GetMapping("/stories")
    public ModelAndView stories(Principal principal) {
        List<Story> entries = commonService.getStories(principal.getName());

        ModelAndView mv = new ModelAndView(USER_CHALLENGES);
        mv.addObject(CHALLENGES, entries);
        return mv;
    }

    @GetMapping("/submitstory")
    public String submitStory() {
        return SUBMIT_STORY_VIEW;
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @PostMapping("/submit-story")
    public ModelAndView submitStory(@RequestParam String challengeId,
            @RequestParam String description,
            @RequestParam MultipartFile[] images,
            @RequestParam String video) {

        ModelAndView mv = new ModelAndView();

        try {
            if (images.length == 0) {
                throw new CommonException("At least 1 image needs to be uploaded");
            }

            Story story = new Story();
            story.setUserId("userId");  // TODO: Get user id from session
            story.setChallengeId(challengeId);
            story.setDescription(description);
            story.setVideo(video);

            for (MultipartFile image : images) {
                StoryImage storyImage = new StoryImage();
                storyImage.setData(image.getBytes());
                story.addImage(storyImage);
            }

            commonService.submitStory(story);

            String successMsg = "Challenge submitted successfully";

            LOG.info(successMsg);
            mv.addObject(MESSAGE, successMsg);
            mv.setViewName(CHALLENGES);
        }
        catch (Exception ex) {
            mv.setViewName(SUBMIT_STORY_VIEW);
            mv.addObject(MESSAGE, ex.getMessage());
        }

        return mv;
    }

    @ResponseBody
    @GetMapping("/gallery")
    public List<byte[]> gallery() {
        try {
            List<Story> latestChallenges = commonService.getLatestChallengesUndertaken();

            // get first image from every story/challenge taken
            return latestChallenges.stream()
                    .map(Story::getImages)
                    .filter(image -> !isEmpty(image))
                    .map(image -> image.get(0).getData())
                    .collect(Collectors.toList());
        }
        catch (Exception ex) {
            String errorMsg = "Failed to get latest challenges undertaken";
            LOG.error(errorMsg, ex);
        }

        return emptyList();
    }

    @GetMapping("/leaderboard")
    public String getLeaderBoardView() {
        return LEADER_BOARD_VIEW;
    }

}
