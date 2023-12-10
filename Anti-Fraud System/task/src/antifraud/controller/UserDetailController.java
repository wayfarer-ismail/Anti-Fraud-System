package antifraud.controller;

import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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

        Optional<UserResponse> savedUser = userDetailsService.registerUser(user);
        if (savedUser.isPresent()) {
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // user already exists
        }
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<?> listUsers() {
        return new ResponseEntity<>(userDetailsService.listUsers(), HttpStatus.OK);
    }
}
