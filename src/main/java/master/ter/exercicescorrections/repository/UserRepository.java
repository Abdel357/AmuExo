package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByLastNameContainingIgnoreCase(String lastName);

    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    User findByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByLastNameContainingOrFirstNameContaining(String lastName, String firstName);

    User findByResetToken(String resetToken);

    boolean existsByEmail(String email);

    List<User> findByFirstNameContainingIgnoreCaseAndRole(String lowerCaseQuery, String enseignant);

    List<User> findByLastNameContainingIgnoreCaseAndRole(String lowerCaseQuery, String enseignant);

    User findByConfirmationToken(String confirmationToken);
}