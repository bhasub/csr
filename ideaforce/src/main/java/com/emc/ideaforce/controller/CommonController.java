package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.model.UserDto;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Common web controller
 */
@Controller
@RequiredArgsConstructor
public class CommonController {

    public static final String CHALLENGES = "challenges";
    public static final String CHALLENGE_DETAIL = "challengedetail";
    public static final String CHALLENGE = "challenge";
    public static final String USER_CHALLENGES = "userchallenges";
    public static final String HOME_VIEW = "index";
    public static final String REGISTRATION_VIEW = "registration";
    public static final String LOGIN_VIEW = "login";
    public static final String GALLERY_VIEW = "gallery";
    public static final String LEADER_BOARD_VIEW = "leaderboard";
    public static final String REDIRECT_DIRECTIVE = "redirect:/";

    @Autowired
    private final CommonService commonService;

    @Autowired
    private final UserService userService;

    Logger logger = LoggerFactory.getLogger(CommonController.class);

    @GetMapping("/registration")
    public ModelAndView showRegistrationForm(Principal principal, ModelMap model) {
        // If user already logged in, do not show registration form and redirect to home page
        if (principal != null) {
            return new ModelAndView(REDIRECT_DIRECTIVE + HOME_VIEW, model);
        }
        else {
            return new ModelAndView(REGISTRATION_VIEW, "user", new UserDto());
        }
    }

    @PostMapping("/registration")
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid UserDto userDto,
            BindingResult result,
            Errors errors,
            HttpServletRequest httpServletRequest) {

        User registered = null;
        // If there are no validation errors, create user
        if (!result.hasErrors()) {
            registered = userService.registerNewUserAccount(userDto);

            // If user could not be created, mostly user/account may already exist
            if (registered == null) {
                result.rejectValue("email", "user.exists");
            }
        }
        if (result.hasErrors() || registered == null) {
            // if any errors, redirect to registration page again
            return new ModelAndView(REGISTRATION_VIEW, "user", userDto);
        }


        // After registration is successful, automatically login user
        try {
            httpServletRequest.login(userDto.getEmail(), userDto.getPassword());
        }
        catch (ServletException e) {
            logger.error("Error while login", e);
            return new ModelAndView(LOGIN_VIEW);
        }
        return new ModelAndView(HOME_VIEW, "user", userDto);
    }

    @RequestMapping("/login")
    public ModelAndView login(Principal principal,
            HttpSession httpSession,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView model = new ModelAndView(LOGIN_VIEW);
        if (logout != null) {
            httpSession.invalidate();
        }
        else if (principal != null) {
            model = new ModelAndView(HOME_VIEW);
        }
        return model;
    }

    @GetMapping("/")
    public String index(Principal principal) {
        return HOME_VIEW;
    }

    @GetMapping("/home")
    public String home(ModelMap model) {
        return HOME_VIEW;
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

    @GetMapping("/gallery")
    public String gallery() {
        return GALLERY_VIEW;
    }

    @GetMapping("/leaderboard")
    public String getLeaderBoardView() {
        return LEADER_BOARD_VIEW;
    }

}
