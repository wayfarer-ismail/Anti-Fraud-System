package antifraud.controller;

import antifraud.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserDetailController {

    @PostMapping("/api/auth/user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return null;
    }
}
