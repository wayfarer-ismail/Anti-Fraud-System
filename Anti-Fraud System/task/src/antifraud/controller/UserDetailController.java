package antifraud.controller;

import antifraud.model.User;
import antifraud.model.request.UserRequest;
import antifraud.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserDetailController {

    UserDetailsServiceImpl userDetailsService;

    public UserDetailController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) {
        User savedUser = userDetailsService.registerUser(user);
        if (savedUser == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        }
    }
}
