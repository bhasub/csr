package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.ChallengeEntry;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.model.UserDto;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityExistsException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private final CommonService commonService;

    @Autowired
    private final UserService userService;

    @RequestMapping(value="/admin", method = GET)
    public String showAdminPage(Model model) {
        return "admin";
    }

    @RequestMapping(value = "/registration", method = GET)
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @RequestMapping(value = "/registration", method = POST)
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid UserDto userDto,
            BindingResult result,
            Errors errors,
            HttpServletRequest httpServletRequest) {

        User registered = new User();
        if (!result.hasErrors()) {
            registered = createUserAccount(userDto, result);
        }
        if (registered == null) {
            result.rejectValue("email", "user.exists");
        }
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", userDto);
        }
        else {
            try {
                httpServletRequest.login(userDto.getEmail(), userDto.getPassword());
            } catch (ServletException e) {
                logger.error("Error while login", e);
                return new ModelAndView("login");
            }
            return new ModelAndView("index", "user", userDto);
        }
    }

    @RequestMapping("/login")
    public ModelAndView login(HttpSession httpSession,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView model = new ModelAndView("login");
        if (logout != null) {
            httpSession.invalidate();
        }
        return model;
    }

    @RequestMapping("/")
    public ModelAndView index(Principal principal) {
        UserDetails userInfo = userService.loadUserByUsername(principal.getName());
        Boolean userIsAdmin = userInfo.getAuthorities().stream().anyMatch(x -> x.getAuthority().equalsIgnoreCase("admin"));
        return new ModelAndView("index", "userIsAdmin", userIsAdmin);
    }

    @RequestMapping("/home")
    public ModelAndView home(Principal principal) {
        UserDetails userInfo = userService.loadUserByUsername(principal.getName());
        Boolean userIsAdmin = userInfo.getAuthorities().stream().anyMatch(x -> x.getAuthority().equalsIgnoreCase("admin"));
        return new ModelAndView("index", "userIsAdmin", userIsAdmin);
    }

    @RequestMapping("/challenges")
    public ModelAndView challengesList(Principal principal) {
        List<ChallengeDetail> challengeDetailList = commonService.getChallengeDetailList();

        ModelAndView mv = new ModelAndView(CHALLENGES);
        mv.addObject(CHALLENGES, challengeDetailList);
        return mv;
    }

    @RequestMapping("/challenges/{id}")
    public ModelAndView challengeDetail(@PathVariable String id) {
        ChallengeDetail challengeDetail = commonService.getChallengeDetail(id);

        ModelAndView mv = new ModelAndView(CHALLENGE_DETAIL);
        mv.addObject(CHALLENGE, challengeDetail);
        return mv;
    }

    @RequestMapping("/stories")
    public ModelAndView challengesTakenList(Principal principal) {
        List<ChallengeEntry> entries = commonService.getChallengesTakenList(principal.getName());

        ModelAndView mv = new ModelAndView(USER_CHALLENGES);
        mv.addObject(CHALLENGES, entries);
        return mv;
    }

    @RequestMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @RequestMapping("/leaderboard")
    public String LeaderBoard() {
        return "leaderboard";
    }

    private User createUserAccount(UserDto userDto, BindingResult result) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(userDto);
        } catch (EntityExistsException e) {
            logger.warn("Error occurred", e);
            return null;
        }
        return registered;
    }

}
