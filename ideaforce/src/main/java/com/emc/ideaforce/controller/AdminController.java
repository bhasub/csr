package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.model.StoryComments;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.List;

import static com.emc.ideaforce.utils.Utils.ADMIN;
import static com.emc.ideaforce.utils.Utils.ADMIN_ROLE;

@Controller
@RequiredArgsConstructor
public class AdminController {

    public static final String ADMIN = "admin";
    public static final String UNAPPROVED_CHALLENGES = "unapprovedchallenges";
    public static final String APPROVE_CHALLENGE = "approved";
    public static final String ADD_COMMENTS_VIEW = "addcomments";
    public static final String VIEW_COMMENTS_VIEW = "viewcomments";

    private final CommonService commonService;

    @Autowired
    private final UserService userService;

    @RolesAllowed(ADMIN_ROLE)
    @GetMapping(value="/admin")
    public ModelAndView showAdminPage() {
        List<Story> unApprovedChallengeDetailList = commonService.findAllByApprovedIsFalse();

        ModelAndView mv = new ModelAndView(ADMIN);
        mv.addObject(UNAPPROVED_CHALLENGES, unApprovedChallengeDetailList );
        return mv;
    }

    @RolesAllowed(ADMIN_ROLE)
    @GetMapping(value="/approve/{id}")
    public String approveStory(@PathVariable String id) {
        commonService.setStoryApproved(id);
        return APPROVE_CHALLENGE;
    }

    @GetMapping(value="/addcomments/{id}")
    public ModelAndView commentsForStory(@PathVariable String id, ModelMap model) {
        CommentDto commentDto = new CommentDto();
        commentDto.setStoryId(id);
        model.addAttribute("newcomment", commentDto);
        return new ModelAndView(ADD_COMMENTS_VIEW, model);
    }

    @PostMapping(value="/addcomments")
    public ModelAndView addCommentsForStory(@ModelAttribute("newcomment") CommentDto commentModel,
                                            Principal principal) {
        User currentUser = userService.getUser(principal.getName());
        commonService.saveStoryComment(commentModel, currentUser);
        return showAdminPage();
    }

    @GetMapping(value="/viewcomments/{id}")
    public ModelAndView viewCommentsForStory(@PathVariable String id) {
        List<StoryComments> storyComments = commonService.getAllCommentsForStory(id);
        ModelAndView mvObject = new ModelAndView(VIEW_COMMENTS_VIEW);
        mvObject.addObject("storyComments", storyComments);
        return mvObject;
    }

}
