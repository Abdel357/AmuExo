package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.AcademicYear;
import master.ter.exercicescorrections.model.Domain;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UeRepositoryTest {

    @Autowired
    private UeRepository ueRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Ue ue;

    @BeforeEach
    void setUp() {
        // Reset DB values
        ueRepository.deleteAll();
        userRepository.deleteAll();

        // Save new User
        user = new User("Doe", "John", "john.doe@univ-amu.fr", "P@ssw0rd", "5678 Another Street", "0987654321", "professor");
        userRepository.save(user);

        // Save new Ue
        Set<String> tags = new HashSet<>();
        tags.add("Math");
        tags.add("Science");
        ue = new Ue("Calculus", Domain.Mathematiques, AcademicYear.Master_1, List.of("SP1", "SP2"), user, tags);
        ueRepository.save(ue);
    }

    @Test
    void findById() {
        assertEquals(ue.getTitle(), ueRepository.findById(ue.getId()).get().getTitle());
    }

    @Test
    void findByCreator() {
        List<Ue> ues = ueRepository.findByCreator(user);
        assertFalse(ues.isEmpty());
        assertEquals(ue.getTitle(), ues.get(0).getTitle());
    }

    @Test
    void findByCreatorId() {
        List<Ue> ues = ueRepository.findByCreatorId(user.getId());
        assertFalse(ues.isEmpty());
        assertEquals(ue.getTitle(), ues.get(0).getTitle());
    }

    @Test
    void findByYear() {
        List<Ue> ues = ueRepository.findByYear(AcademicYear.Master_1);
        assertFalse(ues.isEmpty());
        assertEquals(ue.getYear(), ues.get(0).getYear());
    }

    @Test
    void findByDomain() {
        List<Ue> ues = ueRepository.findByDomain(Domain.Mathematiques);
        assertFalse(ues.isEmpty());
        assertEquals(ue.getDomain(), ues.get(0).getDomain());
    }

    @Test
    void findByTitle() {
        List<Ue> ues = ueRepository.findByTitleContainingIgnoreCase("Calculus");
        assertFalse(ues.isEmpty());
        assertEquals(ue.getTitle(), ues.get(0).getTitle());
    }

    @Test
    void findByTagsContaining() {
        List<Ue> ues = ueRepository.findByTagsContainingIgnoreCase("Math");
        assertFalse(ues.isEmpty());
        assertTrue(ues.get(0).getTags().contains("Math"));

        ues = ueRepository.findByTagsContainingIgnoreCase("Science");
        assertFalse(ues.isEmpty());
        assertTrue(ues.get(0).getTags().contains("Science"));
    }
}