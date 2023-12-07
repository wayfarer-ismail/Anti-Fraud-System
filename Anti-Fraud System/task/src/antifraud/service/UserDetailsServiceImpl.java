package antifraud.service;

import antifraud.model.UserDAO;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDAO user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>()) // add authorities/roles here
                .build();
    }

    public UserResponse registerUser(UserRequest userReq) {
        UserDAO user = new UserDAO(userReq.getName(), userReq.getUsername(), userReq.getPassword());
        UserDAO savedUser = userRepository.save(user);
        UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getUsername());
        return userResponse;
    }
}