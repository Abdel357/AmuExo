package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class QuizzRepositoryTest {

    @Autowired
    private QuizzRepository quizzRepository;

    @Autowired
    private UeRepository ueRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Ue ue;
    private Quizz quizz;

    @BeforeEach
    void setUp() {
        // Reset DB values
        quizzRepository.deleteAll();
        ueRepository.deleteAll();
        userRepository.deleteAll();

        // Save new User
        user = new User("Doe", "John", "john.doe@univ-amu.fr", "P@ssw0rd", "5678 Another Street", "0987654321", "professor");
        userRepository.save(user);

        // Save new Ue
        Set<String> ueTags = new HashSet<>();
        ueTags.add("Math");
        ueTags.add("Science");
        ue = new Ue("Calculus", Domain.Mathematiques, AcademicYear.Licence_3, List.of("SP1", "SP2"), user, ueTags);
        ueRepository.save(ue);

        // Save new Quizz
        Set<String> quizzTags = new HashSet<>();
        quizzTags.add("Algebra");
        quizzTags.add("Geometry");
        quizz = new Quizz("Math Quizz", "Mathematics", quizzTags, null, ue, user);
        quizzRepository.save(quizz);
    }

    @Test
    void findById() {
        assertEquals(quizz.getTitle(), quizzRepository.findById(quizz.getId()).get().getTitle());
    }

    @Test
    void findByUe() {
        List<Quizz> quizzes = quizzRepository.findByUe(ue);
        assertFalse(quizzes.isEmpty());
        assertEquals(quizz.getTitle(), quizzes.get(0).getTitle());
    }

    @Test
    void findByUeId() {
        List<Quizz> quizzes = quizzRepository.findByUeId(ue.getId());
        assertFalse(quizzes.isEmpty());
        assertEquals(quizz.getTitle(), quizzes.get(0).getTitle());
    }

    @Test
    void findByTagsContaining() {
        List<Quizz> quizzes = quizzRepository.findByTagsContainingIgnoreCase("Algebra");
        assertFalse(quizzes.isEmpty());
        assertTrue(quizzes.get(0).getTags().contains("Algebra"));

        quizzes = quizzRepository.findByTagsContainingIgnoreCase("Geometry");
        assertFalse(quizzes.isEmpty());
        assertTrue(quizzes.get(0).getTags().contains("Geometry"));
    }

    @Test
    void findByTitleContaining() {
        List<Quizz> quizzes = quizzRepository.findByTitleContainingIgnoreCase("Math");
        assertFalse(quizzes.isEmpty());
        assertEquals(quizz.getTitle(), quizzes.get(0).getTitle());
    }

    @Test
    void findByCreator() {
        List<Quizz> quizzes = quizzRepository.findByCreator(user);
        assertFalse(quizzes.isEmpty());
        assertEquals(quizz.getTitle(), quizzes.get(0).getTitle());
    }

    @Test
    void findByCreatorId() {
        List<Quizz> quizzes = quizzRepository.findByCreatorId(user.getId());
        assertFalse(quizzes.isEmpty());
        assertEquals(quizz.getTitle(), quizzes.get(0).getTitle());
    }
}