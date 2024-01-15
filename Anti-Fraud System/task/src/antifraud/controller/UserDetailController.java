package antifraud.controller;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserDetailController {
    UserDetailsServiceImpl userDetailsService;

    public UserDetailController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) {
        try {
            UserResponse savedUser = userDetailsService.registerUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        try {
            UserResponse updatedUser = userDetailsService.updateUserRole(request.get("username"), request.get("role"));
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
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
