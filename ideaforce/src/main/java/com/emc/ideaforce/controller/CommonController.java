package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.model.StoryImage;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.UserService;
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
import java.util.stream.Stream;

import static java.lang.String.format;
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
    private static final String PROFILE_VIEW = "profile";
    private static final String MESSAGE = "message";

    private final CommonService commonService;

    private final UserService userService;

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView(HOME_VIEW);
    }

    @GetMapping("/home")
    public ModelAndView home() {
        return new ModelAndView(HOME_VIEW);
    }

    @GetMapping("/challenges")
    public ModelAndView challengesList() {
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

    @PostMapping("/submit-story")
    public ModelAndView submitStory(Principal principal,
            @RequestParam String challengeId,
            @RequestParam String description,
            @RequestParam MultipartFile[] images,
            @RequestParam String video) {

        ModelAndView mv = new ModelAndView();

        try {
            if (images.length == 0 || Stream.of(images).anyMatch(MultipartFile::isEmpty)) {
                throw new CommonException("At least 1 image needs to be uploaded");
            }

            Story story = new Story();
            story.setUserId(principal.getName());
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

    @GetMapping("/profile")
    public ModelAndView profile(Principal principal) {
        ModelAndView mv = new ModelAndView(PROFILE_VIEW);

        try {
            User user = userService.getUser(principal.getName());
            mv.addObject("details", user);

            List<Story> stories = commonService.getStories(principal.getName());
            mv.addObject("stories", stories);
        }
        catch (Exception ex) {
            String errorMessage = "Failed to get profile details";
            LOG.error(errorMessage + " for user {}", principal.getName(), ex);
            mv.addObject(MESSAGE, errorMessage);
        }

        return mv;
    }

    @PostMapping("/profile/set")
    public ModelAndView profile(Principal principal,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String employeeId,
            @RequestParam(required = false) MultipartFile image) {

        ModelAndView mv = new ModelAndView(PROFILE_VIEW);

        try {
            User user = userService.getUser(principal.getName());

            if (user == null) {
                String errorMessage = "User doesn't exist";
                if (LOG.isDebugEnabled()) {
                    LOG.debug(format("%s with ID %s", errorMessage, principal.getName()));
                }

                mv.addObject(MESSAGE, errorMessage);
            }
            else {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmployeeId(employeeId);

                if (!image.isEmpty()) {
                    user.setImage(image.getBytes());
                }

                userService.updateProfile(user);

                String successMessage = "Profile updated successfully";
                LOG.info(successMessage);
                mv.addObject(MESSAGE, successMessage);
            }
        }
        catch (Exception ex) {
            String errorMessage = "Failed to update profile";
            LOG.error(errorMessage, ex);
            mv.addObject(MESSAGE, errorMessage);
        }

        return mv;
    }

}
