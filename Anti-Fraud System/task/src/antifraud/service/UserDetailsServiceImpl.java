package antifraud.service;

import antifraud.config.PasswordEncoderConfig;
import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.UserDAO;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.repository.UserRepository;
import antifraud.service.adapter.UserAdapter;
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

    /**
     * Registers a user.
     * Validates the fields of the user request, converts it to a UserDAO object,
     *  and saves it to the database.
     *
     * @param userReq The user request to register.
     * @return The response of the registered user.
     * @throws BadRequestException If any of the fields are invalid.
     * @throws ConflictException If the user already exists.
     */
    @Transactional
    public UserResponse registerUser(UserRequest userReq) {
        if (userReq.hasEmptyFields()) {
            throw new BadRequestException("Empty fields!");
        }

        UserDAO user = new UserDAO(userReq.name(), userReq.username(),
                passwordEncoder.passwordEncoder().encode(userReq.password()));

        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new ConflictException("User already exists!");
        }

        if (userRepository.count() == 0) { // First user is an administrator
            user.setRole("ADMINISTRATOR");
            user.setAccountNonLocked(true);
        } else {
            user.setRole("MERCHANT");
        }

        UserDAO savedUser = userRepository.save(user);
        return savedUser.toUserResponse();
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

    /**
     * Deletes a user by username ignoring case.
     *
     * @param username The username of the user to delete.
     * @return The number of deleted users.
     * @throws BadRequestException If the user is an administrator.
     */
    @Transactional
    public Integer deleteUser(String username) {
        Optional<UserDAO> user = userRepository.findByUsernameIgnoreCase(username);

        if (user.isPresent() && user.get().getRole().equals("ADMINISTRATOR")) {
            throw new BadRequestException("Cannot delete administrator account!");
        }
        return userRepository.deleteByUsernameIgnoreCase(username);
    }

    /**
     * Updates the role of a user.
     *
     * @param username The username of the user to update.
     * @param role The new role of the user.
     * @return The response of the updated user.
     * @throws NotFoundException If the user is not found.
     * @throws BadRequestException If the role is invalid.
     * @throws ConflictException If the user already has the role.
     */
    @Transactional
    public UserResponse updateUserRole(String username, String role) {
        UserDAO user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));

        if (!role.matches("SUPPORT|MERCHANT")) {
            throw new BadRequestException("Invalid role!");
        }

        if (user.getRole().equals(role)) {
            throw new ConflictException("User already has this role!");
        }

        user.setRole(role);
        UserDAO updatedUser = userRepository.save(user);
        return updatedUser.toUserResponse();
    }

    /**
     * Updates the lock status of a user.
     *
     * @param username The username of the user to update.
     * @param operation lock or unlock.
     * @return The response of the updated user.
     * @throws NotFoundException If the user is not found.
     * @throws BadRequestException If the user is an administrator.
     */
    @Transactional
    public UserResponse updateUserLock(String username, String operation) {
        UserDAO user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

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
}
