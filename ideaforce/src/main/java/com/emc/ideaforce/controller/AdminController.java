package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.model.StoryComments;
import com.emc.ideaforce.model.StoryImage;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.emc.ideaforce.utils.Utils.ADMIN_ROLE;

@Controller
@RequiredArgsConstructor
public class AdminController {

    public static final String ADMIN = "admin";
    public static final String UNAPPROVED_CHALLENGES = "unapprovedchallenges";
    public static final String ADD_COMMENTS_VIEW = "addcomments";
    public static final String VIEW_COMMENTS_VIEW = "viewcomments";
    private static final String VIEW_STORY_DETAILS_ADMIN_VIEW = "storydetailsForAdmin";
    private static final String VIEW_STORY_DETAILS_VIEW = "storydetails";

    private final CommonService commonService;

    @Autowired
    private final UserService userService;

    @RolesAllowed(ADMIN_ROLE)
    @GetMapping(value = "/admin", produces = "application/json")
    public ModelAndView showAdminPage() {
        List<Story> unApprovedChallengeDetailList = commonService.getUnapprovedStories();

        ModelAndView mv = new ModelAndView(ADMIN);
        mv.addObject(UNAPPROVED_CHALLENGES, unApprovedChallengeDetailList);
        return mv;
    }

    @RolesAllowed(ADMIN_ROLE)
    @GetMapping(value = "/approve/{id}", produces = "application/json")
    public ModelAndView approveStory(@PathVariable String id) {
        commonService.approveStory(id);
        ModelAndView mv = new ModelAndView(ADMIN + " :: resultsList");
        List<Story> unApprovedChallengeDetailList = commonService.getUnapprovedStories();
        mv.addObject(UNAPPROVED_CHALLENGES, unApprovedChallengeDetailList);

        return mv;
    }

    @GetMapping(value = "/addcomments/{id}")
    public ModelAndView commentsForStory(@PathVariable String id, ModelMap model) {
        CommentDto commentDto = new CommentDto();
        commentDto.setStoryId(id);
        model.addAttribute("newcomment", commentDto);
        return new ModelAndView(ADD_COMMENTS_VIEW, model);
    }

    @PostMapping(value = "/addcomments")
    public ModelAndView addCommentsForStory(Principal principal,
                                            @ModelAttribute("storyComment") StoryComments commentModel) {
        User currentUser = userService.getUser(principal.getName());
        StoryComments comment = commonService.getCommentForStory(commentModel.getStoryId());
        if(comment != null) {
            String commentId = comment.getId() + "";
            commentModel.setId(Integer.parseInt(commentId));
        }
        commonService.saveStoryComment(commentModel, currentUser);
        return showAdminPage();
    }

    @GetMapping(value = "/viewcomments/{id}")
    public ModelAndView viewCommentForStory(@PathVariable String id) {
        ModelAndView mvObject = new ModelAndView(VIEW_COMMENTS_VIEW);
        StoryComments storyComment = commonService.getCommentForStory(id);

        if(storyComment == null){
            storyComment = new StoryComments();
            storyComment.setStoryId(id);
        }

        mvObject.addObject("storyComment", storyComment);

        return mvObject;
    }

    @GetMapping(value = "viewdetails/type/{type}/{id}")
    public ModelAndView getStoryDetailsForAdmin(Principal principal, @PathVariable String type, @PathVariable String id) {
        Story storyObj = commonService.getStoryById(id);
        List<StoryImage> images = storyObj.getImages();
        List<String> encodedImages = new ArrayList<>();
        for (StoryImage image : images) {
            encodedImages.add(Base64.getEncoder().encodeToString(image.getData()));
        }

        ModelAndView mvObject;
        if(type.equals("admin")){
            mvObject = new ModelAndView(VIEW_STORY_DETAILS_ADMIN_VIEW);
        } else {
            mvObject = new ModelAndView(VIEW_STORY_DETAILS_VIEW);
        }
        mvObject.addObject("storyDetails", storyObj);
        mvObject.addObject("storyPics", encodedImages);
        mvObject.addObject("user",principal.getName());
        return mvObject;
    }
}