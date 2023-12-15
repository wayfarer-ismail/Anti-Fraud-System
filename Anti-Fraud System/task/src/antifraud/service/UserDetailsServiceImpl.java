package antifraud.service;

import antifraud.config.PasswordEncoderConfig;
import antifraud.model.UserDAO;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoderConfig passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoderConfig passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public Optional<UserResponse> registerUser(UserRequest userReq) {
        UserDAO user = new UserDAO(userReq.getName(), userReq.getUsername(), passwordEncoder.passwordEncoder().encode(userReq.getPassword()));
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            return Optional.empty();
        }

        UserDAO savedUser = userRepository.save(user);
        UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getUsername());
        return Optional.of(userResponse);
    }

    public List<UserResponse> listUsers() {
        List<UserDAO> users = userRepository.findAll();
        users.sort(Comparator.comparing(UserDAO::getId));
        //users.stream().map(user -> new UserResponse(user.getId(), user.getName(), user.getUsername())).toList();
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserDAO user : users) {
            userResponses.add(new UserResponse(user.getId(), user.getName(), user.getUsername()));
        }
        return userResponses;
    }

    public boolean deleteUser(String username) {
        return userRepository.deleteByNameIgnoreCase(username);
    }
}