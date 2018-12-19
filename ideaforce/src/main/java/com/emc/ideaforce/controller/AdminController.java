package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.model.StoryComments;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.UserService;
import com.emc.ideaforce.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequiredArgsConstructor
public class AdminController {

    public static final String ADMIN = "admin";
    public static final String UNAPPROVED_CHALLENGES = "unapprovedchallenges";
    public static final String APPROVE_CHALLENGE = "approved";
    public static final String ADD_COMMENTS_VIEW = "addcomments";
    public static final String VIEW_COMMENTS_VIEW = "viewcomments";

    @Autowired
    private final CommonService commonService;

    @Autowired
    private final UserService userService;


    @RequestMapping(value="/admin", method = GET)
    @RolesAllowed(Utils.ADMIN_ROLE)
    public ModelAndView showAdminPage() {
        List<Story> unApprovedChallengeDetailList = commonService.findAllByApprovedIsFalse();

        ModelAndView mv = new ModelAndView(ADMIN);
        mv.addObject(UNAPPROVED_CHALLENGES, unApprovedChallengeDetailList );
        return mv;
    }

    @RequestMapping(value="/approve/{id}", method = GET)
    @RolesAllowed(Utils.ADMIN_ROLE)
    public String approveStory(@PathVariable String id) {
        commonService.setStoryApproved(id);
        return APPROVE_CHALLENGE;
    }

    @RequestMapping(value="/addcomments/{id}", method = GET)
    public ModelAndView commentsForStory(@PathVariable String id, ModelMap model) {
        CommentDto commentDto = new CommentDto();
        commentDto.setStoryId(id);
        model.addAttribute("newcomment", commentDto);
        return new ModelAndView(ADD_COMMENTS_VIEW, model);
    }

    @RequestMapping(value="/addcomments", method = POST)
    public ModelAndView addCommentsForStory(@ModelAttribute("newcomment") CommentDto commentModel,
                                            Principal principal) {
        User currentUser = userService.getUser(principal.getName());
        commonService.saveStoryComment(commentModel, currentUser);
        return showAdminPage();
    }

    @RequestMapping(value="/viewcomments/{id}", method = GET)
    public ModelAndView viewCommentsForStory(@PathVariable String id) {
        List<StoryComments> storyComments = commonService.getAllCommentsForStory(id);
        ModelAndView mvObject = new ModelAndView(VIEW_COMMENTS_VIEW);
        mvObject.addObject("storyComments", storyComments);
        return mvObject;
    }

}
