package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.List;

import static com.emc.ideaforce.utils.Utils.ADMIN;
import static com.emc.ideaforce.utils.Utils.ADMIN_ROLE;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private static final String UNAPPROVED_CHALLENGES = "unapprovedchallenges";

    private final CommonService commonService;

    @RolesAllowed(ADMIN_ROLE)
    @GetMapping(value="/admin")
    public ModelAndView showAdminPage(Principal principal) {
        List<Story> unApprovedChallengeDetailList = commonService.findAllByApprovedIsFalse();

        ModelAndView mv = new ModelAndView(ADMIN);
        mv.addObject(UNAPPROVED_CHALLENGES, unApprovedChallengeDetailList );
        return mv;
    }

    @RolesAllowed(ADMIN_ROLE)
    @GetMapping(value="/approve/{id}")
    public String approveStory(Principal principal, @PathVariable String id) {
        commonService.setStoryApproved(id);
        return "approved";
    }

}
