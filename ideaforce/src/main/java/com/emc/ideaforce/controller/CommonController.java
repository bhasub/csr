package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.ChallengeDetail;
import com.emc.ideaforce.model.Story;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.service.CommonService;
import com.emc.ideaforce.service.MailService;
import com.emc.ideaforce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static com.emc.ideaforce.utils.Utils.PWD_PRIVELEGE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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

    public static final String HOME_VIEW = "index";
    public static final String REGISTRATION_VIEW = "registration";
    public static final String LOGIN_VIEW = "login";
    public static final String GALLERY_VIEW = "gallery";
    public static final String LEADER_BOARD_VIEW = "leaderboard";
    public static final String REDIRECT_DIRECTIVE = "redirect:/";
    public static final String UPDATE_PASSWORD_VIEW = "updatepassword";
    public static final String FORGOT_PASSWORD_VIEW = "forgotpassword";
    public static final String MESSAGE = "message";

    @Autowired
    private final CommonService commonService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final MailService mailService;

    private static String getPwdResetUrl(HttpServletRequest request, User user, String token) {
        return getBaseUrl(request) + "/user/changePassword?email=" + user.getEmail() + "&token=" + token;
    }

    private static String getBaseUrl(HttpServletRequest req) {
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }

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
    public ModelAndView index(Principal principal) {
        return new ModelAndView("index");
    }

    @RequestMapping("/home")
    public ModelAndView home(Principal principal) {
        return new ModelAndView("index");
    }

    @GetMapping("/user/forgotPassword")
    public String password() {
        return FORGOT_PASSWORD_VIEW;
    }

    @PostMapping("/user/resetPassword")
    public ModelAndView resetPassword(HttpServletRequest request,
            @RequestParam("email") String userEmail) {

        User user = userService.getUser(userEmail);
        if (user == null) {
            return new ModelAndView("/user/resetPassword", MESSAGE, "User not found");
        }

        String token = userService.createPasswordResetRequestForUser(user);
        mailService.sendSimpleMessage(user.getEmail(), "Reset password",
                "Reset password " + getPwdResetUrl(request, user, token));
        return new ModelAndView(FORGOT_PASSWORD_VIEW, MESSAGE,
                "Password reset mail sent successfully, check your inbox.");
    }

    @GetMapping(value = "/user/changePassword")
    public ModelAndView showChangePasswordPage(ModelMap model,
            @RequestParam("email") String email,
            @RequestParam("token") String token) {
        String result = userService.validatePasswordResetToken(email, token);

        if (result != null) {
            return new ModelAndView(FORGOT_PASSWORD_VIEW, model);
        }
        model.addAttribute("newpassword", new PasswordDto());
        return new ModelAndView(UPDATE_PASSWORD_VIEW, model);
    }

    @PostMapping("/user/savePassword")
    @RolesAllowed(PWD_PRIVELEGE)
    public ModelAndView savePassword(@ModelAttribute("newpassword") @Valid PasswordDto passwordDto,
            BindingResult result,
            Errors errors,
            HttpServletRequest httpServletRequest) {
        if (!result.hasErrors()) {
            User user = (User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            userService.changeUserPassword(user, passwordDto.getPassword());
            httpServletRequest.getSession().invalidate();
            return new ModelAndView(LOGIN_VIEW, MESSAGE, "Password reset successful");
        }
        else {
            return new ModelAndView(UPDATE_PASSWORD_VIEW, "newpassword", passwordDto);
        }
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
