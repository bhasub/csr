package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.UserService;
import com.emc.ideaforce.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequiredArgsConstructor
public class AdminController {

    public static final String ADMIN = "admin";
    public static final String UNAPPROVED_CHALLENGES = "unapprovedchallenges";

    @Autowired
    private final CommonService commonService;

    @Autowired
    private final UserService userService;


    @RequestMapping(value="/admin", method = GET)
    @RolesAllowed(Utils.ADMIN_ROLE)
    public ModelAndView showAdminPage(Principal principal) {
        List<Story> unApprovedChallengeDetailList = commonService.findAllByApprovedIsFalse();

        ModelAndView mv = new ModelAndView(ADMIN);
        mv.addObject(UNAPPROVED_CHALLENGES, unApprovedChallengeDetailList );
        return mv;
    }

    @RequestMapping(value="/approve/{id}", method = GET)
    @RolesAllowed(Utils.ADMIN_ROLE)
    public String approveStory(Principal principal, @PathVariable String id) {
        commonService.setStoryApproved(id);
        return "approved";
    }

}
