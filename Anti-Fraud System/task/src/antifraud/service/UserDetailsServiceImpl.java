package antifraud.service;

import antifraud.config.PasswordEncoderConfig;
import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.model.UserDAO;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.repository.UserRepository;
import antifraud.service.adapter.UserAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

        saveCurrentUser("register");

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
        saveCurrentUser("list");

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

    public UserResponse updateUserRole(String username, String role) {
        UserDAO user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!role.matches("SUPPORT|MERCHANT")) {
            throw new BadRequestException("Invalid role!");
        }

        if (user.getRole().equals(role)) {
            throw new ConflictException("User already has this role!");
        }

        user.setRole(role);
        if (role.equals("SUPPORT")) {
            user.setAccountNonLocked(true);
        }
        //user.setAccountNonLocked(true);
        UserDAO updatedUser = userRepository.save(user);
        return updatedUser.toUserResponse();
    }

    public UserResponse updateUserLock(String username, String operation) {
        UserDAO user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        saveCurrentUser(operation.toLowerCase() + " " + user.getUsername() + " " + user.getRole());

        if (user.getRole().equals("ADMINISTRATOR")) {
            throw new BadRequestException("Cannot lock/unlock administrator account!");
        }
        if (operation.equalsIgnoreCase("LOCK")) {
            user.setAccountNonLocked(false);
        } else if (operation.equalsIgnoreCase("UNLOCK")) {
            user.setAccountNonLocked(true);
        }
        UserDAO updatedUser = userRepository.save(user);
        return updatedUser.toUserResponse();
    }

    public static void saveCurrentUser(String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String info = "anon";
        String filePath = "logs.txt";

        if (!(auth.getPrincipal() instanceof String)) {
            info = auth.getPrincipal().toString();
        }

        try (FileWriter out = new FileWriter(filePath, true)) {
            out.write( action + ": " + info + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}