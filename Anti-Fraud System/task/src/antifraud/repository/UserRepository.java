package antifraud.repository;

import antifraud.model.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDAO, Long> {
    Optional<UserDAO> findByUsernameIgnoreCase(String username);

    /**
     * Deletes a user by username ignoring case
     * @return number of deleted users
     */
    Integer deleteByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}