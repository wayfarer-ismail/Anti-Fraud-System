package antifraud.controller;

import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserDetailController {

    UserDetailsServiceImpl userDetailsService;

    public UserDetailController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/user")
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

    @GetMapping("/list")
    public ResponseEntity<?> listUsers() {
        return new ResponseEntity<>(userDetailsService.listUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        if (userDetailsService.deleteUser(username) > 0) {
            return new ResponseEntity<>(Map.of("username", username, "status", "Deleted successfully!"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/role")
    public ResponseEntity<?> updateUserRole(@RequestBody Map<String, String> request) {
        if (request.containsKey("username") && request.containsKey("role")) {
            UserResponse updatedUser = userDetailsService.updateUserRole(request.get("username"), request.get("role"));
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/access")
    public ResponseEntity<?> updateUserLock(@RequestBody Map<String, String> request) {
        if (request.containsKey("username") && request.containsKey("operation")) {
            UserResponse updatedUser = userDetailsService.updateUserLock(request.get("username"), request.get("operation"));
            return new ResponseEntity<>(Map.of(
                    "status",
                    String.format("User %s %sed!", updatedUser.username(), request.get("operation").toLowerCase())
            ),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
