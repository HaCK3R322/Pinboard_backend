package com.androsov.pinboard.controllers;

import com.androsov.pinboard.entities.User;
import com.androsov.pinboard.exceptions.NoAccessException;
import com.androsov.pinboard.exceptions.NotFoundException;
import com.androsov.pinboard.servicies.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@CrossOrigin
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleNoAccessException(NoAccessException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    // POST mapping method for user registration, that creates user
    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) {
        return userService.save(user);
    }

    // POST mapping to change username of user (only for authenticated user)
    @PostMapping("/user/change/username")
    public User changeUsername(@RequestBody String newUsername, Principal principal) {
        User newUser = userService.changeUsername(principal.getName(), newUsername);

        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        return newUser;
    }

    // POST mapping to change password
    @PostMapping("/user/change/password")
    public User changePassword(@Valid @RequestBody PasswordChangePostBodyJsonMappingClass passwords) {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        return userService.changePassword(passwords.user, passwords.newPassword);
    }
    @AllArgsConstructor
    private static class PasswordChangePostBodyJsonMappingClass { // https://www.youtube.com/watch?v=04xRauelHnw&ab_channel=INSTASAMKA
        @Getter @Setter public User user;
        @Getter @Setter public String oldPassword;
        @Getter @Setter public String newPassword;
    }

}
