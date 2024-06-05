package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        //Reset DB values
        userRepository.deleteAll();

        //Save new User
        user = new User("Doe", "John", "john.doe@univ-amu.fr", "P@ssw0rd", "1234 My Street", "1234567890", "teacher");

        userRepository.save(user);
    }

    @Test
    void findById() {
        assertEquals(user.getLastName(), userRepository.findById(user.getId()).get().getLastName());
    }

    @Test
    void findByLastNameContaining() {
        List<User> users = userRepository.findByLastNameContainingIgnoreCase("Doe");
        assertFalse(users.isEmpty());
        assertEquals(user.getLastName(), users.get(0).getLastName());

        users = userRepository.findByLastNameContainingIgnoreCase("Do");
        assertFalse(users.isEmpty());
        assertEquals(user.getLastName(), users.get(0).getLastName());
    }

    @Test
    void findByFirstNameContaining() {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCase("John");
        assertFalse(users.isEmpty());
        assertEquals(user.getFirstName(), users.get(0).getFirstName());

        users = userRepository.findByFirstNameContainingIgnoreCase("Jo");
        assertFalse(users.isEmpty());
        assertEquals(user.getFirstName(), users.get(0).getFirstName());
    }

    @Test
    void findByEmail() {
        User foundUser = userRepository.findByEmail("john.doe@univ-amu.fr");
        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findByRole() {
        List<User> users = userRepository.findByRole("teacher");
        assertFalse(users.isEmpty());
        assertEquals(user.getRole(), users.get(0).getRole());
    }
}