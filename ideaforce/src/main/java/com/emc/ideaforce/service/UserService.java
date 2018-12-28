package com.emc.ideaforce.service;

import com.emc.ideaforce.controller.UserDto;
import com.emc.ideaforce.model.PasswordResetRequest;
import com.emc.ideaforce.model.User;
import com.emc.ideaforce.repository.PasswordResetRepository;
import com.emc.ideaforce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.emc.ideaforce.utils.Utils.ADMIN;
import static com.emc.ideaforce.utils.Utils.ADMIN_ROLE;
import static com.emc.ideaforce.utils.Utils.CP_PRIVILEGE;
import static com.emc.ideaforce.utils.Utils.REG_USER_ROLE;
import static java.util.Collections.singletonList;

public class UserService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static List<GrantedAuthority> getGrantedAuthorities(String[] roles) {
        if (roles == null || roles.length == 0) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    @Transactional
    public User registerNewUserAccount(UserDto userDtoToRegister) {

        if (userExists(userDtoToRegister.getEmail())) {
            LOG.warn("User `{}` already exists", userDtoToRegister.getEmail());
            return null;
        }
        User user = new User();
        user.setFirstName(userDtoToRegister.getFirstName());
        user.setLastName(userDtoToRegister.getLastName());
        user.setEmployeeId(userDtoToRegister.getEmployeeId());
        user.setPassword(passwordEncoder.encode(userDtoToRegister.getPassword()));
        user.setEmail(userDtoToRegister.getEmail().toLowerCase());
        user.setRoles(new String[] {REG_USER_ROLE});
        User registered = repository.save(user);
        LOG.info("Created user {}", user.getEmail());

        return registered;
    }

    public boolean userExists(String email) {
        Optional<User> user = repository.findById(email.toLowerCase());
        return user.isPresent();
    }

    public User getUser(String email) {
        Optional<User> userOptional = repository.findById(email.toLowerCase());
        return userOptional.orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        //check db first
        User user = getUser(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                    getGrantedAuthorities(user.getRoles()));
        }

        // check whether it is default admin user
        if (username.equalsIgnoreCase(ADMIN)) {
            return new org.springframework.security.core.userdetails.User(ADMIN,
                    passwordEncoder.encode("crossroads"),
                    singletonList(new SimpleGrantedAuthority(ADMIN_ROLE)));
        }

        //otherwise throw error
        throw new UsernameNotFoundException(username);
    }

    public String createPasswordResetRequestForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetRequest myToken = new PasswordResetRequest(token, user);
        passwordResetRepository.save(myToken);

        return token;
    }

    public String validatePasswordResetToken(String id, String token) {
        PasswordResetRequest passToken = passwordResetRepository.findByToken(token);
        if ((passToken == null) || !(passToken.getUser().getEmail().equals(id))) {
            return "Invalid Token";
        }

        if (passToken.isExpired()) {
            return "Token expired";
        }

        User user = passToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, singletonList(new SimpleGrantedAuthority(CP_PRIVILEGE)));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }

    public void changeUserPassword(User user, String password) {
        // Update password
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
    }

}