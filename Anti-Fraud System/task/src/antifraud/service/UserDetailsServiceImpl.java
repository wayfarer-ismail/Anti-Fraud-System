package antifraud.service;

import antifraud.config.PasswordEncoderConfig;
import antifraud.exception.BadRequestException;
import antifraud.model.UserDAO;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.repository.UserRepository;
import antifraud.service.adapter.UserAdapter;
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

        return new UserAdapter(user);
    }

    public Optional<UserResponse> registerUser(UserRequest userReq) {
        UserDAO user = new UserDAO(userReq.name(), userReq.username(), passwordEncoder.passwordEncoder().encode(userReq.password()));
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            return Optional.empty();
        }

        if (userRepository.count() == 0) {
            user.setRole("ADMINISTRATOR");
            user.setAccountNonLocked(true);
        } else {
            user.setRole("MERCHANT");
        }

        UserDAO savedUser = userRepository.save(user);
        UserResponse userResponse = savedUser.toUserResponse();
        return Optional.of(userResponse);
    }

    public List<UserResponse> listUsers() {
        List<UserDAO> users = userRepository.findAll();
        users.sort(Comparator.comparing(UserDAO::getId));
        //users.stream().map(user -> new UserResponse(user.getId(), user.getName(), user.getUsername())).toList();
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserDAO user : users) {
            userResponses.add(user.toUserResponse());
        }
        return userResponses;
    }

    @Transactional
    public Integer deleteUser(String username) {
        return userRepository.deleteByUsernameIgnoreCase(username);
    }

    public Optional<UserResponse> updateUserRole(String username, String role) {
        Optional<UserDAO> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            user.get().setRole(role);
            UserDAO updatedUser = userRepository.save(user.get());
            return Optional.of(updatedUser.toUserResponse());
        } else {
            return Optional.empty();
        }
    }

    public Optional<UserResponse> updateUserLock(String username, String operation) {
        Optional<UserDAO> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            if (user.get().getRole().equals("ADMINISTRATOR")) {
                throw new BadRequestException("Cannot lock/unlock administrator account!");
            }
            if (operation.equals("LOCK")) {
                user.get().setAccountNonLocked(false);
            } else if (operation.equals("UNLOCK")) {
                user.get().setAccountNonLocked(true);
            }
            UserDAO updatedUser = userRepository.save(user.get());
            return Optional.of(updatedUser.toUserResponse());
        } else {
            return Optional.empty();
        }
    }
}