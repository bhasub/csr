package com.emc.ideaforce.controller;

import com.emc.ideaforce.model.User;
import com.emc.ideaforce.service.MailService;
import com.emc.ideaforce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.emc.ideaforce.utils.Utils.CP_PRIVILEGE;

/**
 * Controller related to user registration, login and security
 */
@Controller
@RequiredArgsConstructor
public class SecurityController {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityController.class);

    private static final String HOME_VIEW = "index";
    private static final String REGISTRATION_VIEW = "registration";
    private static final String LOGIN_VIEW = "login";
    private static final String REDIRECT_DIRECTIVE = "redirect:/";
    private static final String UPDATE_PASSWORD_VIEW = "updatepassword";
    private static final String FORGOT_PASSWORD_VIEW = "forgotpassword";
    private static final String MESSAGE = "message";
    public static final String VERIFY_EMAIL_VIEW = "verify";
    public static final String USER_MODEL = "user";
    public static final String VERIFY_EMAIL_MODEL = "verifyEmail";

    private final UserService userService;

    private final MailService mailService;

    private Map<String, EmailVerificationDto> verificationCodeMap = new HashMap<>();

    @GetMapping("/verify")
    public ModelAndView showEmailVerifyForm(Principal principal, ModelMap model) {
        if (principal != null) {
            return new ModelAndView(REDIRECT_DIRECTIVE + HOME_VIEW, model);
        }
        return new ModelAndView(VERIFY_EMAIL_VIEW, VERIFY_EMAIL_MODEL, new EmailVerificationDto());
    }

    @PostMapping("/verify")
    public ModelAndView verifyEmail(
            @ModelAttribute(VERIFY_EMAIL_MODEL) @Valid EmailVerificationDto emailVerificationDto,
            BindingResult result,
            Errors errors,
            HttpServletRequest httpServletRequest) {
        // check whether email is already registered
        if (userService.userExists(emailVerificationDto.getEmailId())) {
            result.rejectValue("emailId", "email.exists");
        }
        else {
            //Check if code is already generated
            EmailVerificationDto generated = verificationCodeMap.get(emailVerificationDto.getEmailId());

            if (generated != null) {
                // if verified, proceed to registration
                if (generated.getVerificationCode().equals(emailVerificationDto.getVerificationCode())) {
                    UserDto userDto = new UserDto();
                    userDto.setEmail(emailVerificationDto.getEmailId());
                    String[] names = emailVerificationDto.getEmailId().split("[\\.\\@]");
                    userDto.setFirstName(names[0]);
                    userDto.setLastName(names[1]);
                    return new ModelAndView(REGISTRATION_VIEW, USER_MODEL, userDto);
                }
                else {
                    result.rejectValue("verificationCode", "email.verify.failed");
                }
            }
            else {
                //generate code and send mail
                String code = UUID.randomUUID().toString();
                verificationCodeMap.put(emailVerificationDto.getEmailId(),
                        new EmailVerificationDto(emailVerificationDto.getEmailId(), code, true));
                mailService.sendSimpleMessage(emailVerificationDto.getEmailId(),
                        "Email Verification Code",
                        "Your email verification code is : " + code);
            }

            emailVerificationDto.setCodeGenerated(true);
        }
        return new ModelAndView(VERIFY_EMAIL_VIEW, VERIFY_EMAIL_MODEL, emailVerificationDto);
    }

    @GetMapping("/registration")
    public ModelAndView showRegistrationForm(@RequestParam(value = "email", required = false) String email,
            Principal principal, ModelMap model) {
        // If user already logged in, do not show registration form and redirect to home page
        if (principal != null) {
            return new ModelAndView(REDIRECT_DIRECTIVE + HOME_VIEW, model);
        }
        else {
            // If email is already verified then show the registration form
            if (email != null && verificationCodeMap.containsKey(email)) {
                return new ModelAndView(REGISTRATION_VIEW, USER_MODEL, new UserDto());
            }
            else {
                // First step of registration
                return showEmailVerifyForm(principal, model);
            }
        }
    }

    @PostMapping("/registration")
    public ModelAndView registerUserAccount(
            @ModelAttribute(USER_MODEL) @Valid UserDto userDto,
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
            return new ModelAndView(REGISTRATION_VIEW, USER_MODEL, userDto);
        }


        // After registration is successful, automatically login user
        try {
            httpServletRequest.login(userDto.getEmail(), userDto.getPassword());
        }
        catch (ServletException e) {
            LOG.error("Error while login", e);
            return new ModelAndView(LOGIN_VIEW);
        }
        return new ModelAndView(HOME_VIEW, USER_MODEL, userDto);
    }

    @GetMapping("/login")
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
                "To reset your 'Ideaforce - Around the World' portal account password, click " + getPwdResetUrl(request, user,
                        token));
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
    @RolesAllowed(CP_PRIVILEGE)
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

    private static String getPwdResetUrl(HttpServletRequest request, User user, String token) {
        return getBaseUrl(request) + "/user/changePassword?email=" + user.getEmail() + "&token=" + token;
    }

    private static String getBaseUrl(HttpServletRequest req) {
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }

}
