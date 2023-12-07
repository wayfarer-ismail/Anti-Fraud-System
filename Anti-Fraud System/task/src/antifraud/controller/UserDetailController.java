package antifraud.controller;

import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserDetailController {

    UserDetailsServiceImpl userDetailsService;

    public UserDetailController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) {
        if (user.hasEmptyFields()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserResponse savedUser = userDetailsService.registerUser(user);
        if (savedUser == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        }
    }
}
