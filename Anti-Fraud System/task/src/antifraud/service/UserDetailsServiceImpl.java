package antifraud.service;

import antifraud.config.PasswordEncoderConfig;
import antifraud.model.UserDAO;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole()) // add authorities/roles here
                .build();
    }

    public Optional<UserResponse> registerUser(UserRequest userReq) {
        UserDAO user = new UserDAO(userReq.name(), userReq.username(), passwordEncoder.passwordEncoder().encode(userReq.password()));
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            return Optional.empty();
        }

        if (userRepository.count() == 0) {
            user.setRole("ROLE_ADMINISTRATOR");
        } else {
            user.setRole("ROLE_MERCHANT");
        }

        UserDAO savedUser = userRepository.save(user);
        UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getUsername(), savedUser.getRole());
        return Optional.of(userResponse);
    }

    public List<UserResponse> listUsers() {
        List<UserDAO> users = userRepository.findAll();
        users.sort(Comparator.comparing(UserDAO::getId));
        //users.stream().map(user -> new UserResponse(user.getId(), user.getName(), user.getUsername())).toList();
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserDAO user : users) {
            userResponses.add(new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole()));
        }
        return userResponses;
    }

    @Transactional
    public Integer deleteUser(String username) {
        return userRepository.deleteByUsernameIgnoreCase(username);
    }
}