package com.emc.ideaforce.service;

import com.emc.ideaforce.model.User;
import com.emc.ideaforce.model.UserDto;
import com.emc.ideaforce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(UserService.class);


    @Transactional
    public User registerNewUserAccount(UserDto userDtoToRegister) {
        if (emailExist(userDtoToRegister.getEmail())) {
            throw new EntityExistsException("User already exists with given email");
        }
        User user = new User();
        user.setFirstName(userDtoToRegister.getFirstName());
        user.setLastName(userDtoToRegister.getLastName());
        user.setPassword(passwordEncoder.encode(userDtoToRegister.getPassword()));
        user.setEmail(userDtoToRegister.getEmail().toLowerCase());
        user.setRoles(userDtoToRegister.getRoles());

        User registered = repository.save(user);
        logger.info("Created user {}", user.getEmail());
        return registered;
    }

    private boolean emailExist(String email) {
        Optional<User> user = repository.findById(email.toLowerCase());
        return user.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findById(username.toLowerCase());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user.getRoles()));
        }
        // default admin user
        if (username.equalsIgnoreCase("admin")) {
            return new org.springframework.security.core.userdetails.User("admin",
                        passwordEncoder.encode("crossroads"),
                        Arrays.asList(new SimpleGrantedAuthority("ADMIN")));
        }
        throw new UsernameNotFoundException(username);
    }

    private static List<GrantedAuthority> getGrantedAuthorities(String[] roles) {
        if(roles == null || roles.length == 0) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}