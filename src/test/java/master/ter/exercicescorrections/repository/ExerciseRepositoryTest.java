package master.ter.exercicescorrections.repository;


import master.ter.exercicescorrections.model.Exercise;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static master.ter.exercicescorrections.model.AcademicYear.*;
import static master.ter.exercicescorrections.model.Domain.Informatique;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UeRepository ueRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Ue ue;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        // Reset DB values
        exerciseRepository.deleteAll();
        ueRepository.deleteAll();
        userRepository.deleteAll();

        // Save new User
        user = new User("Doe", "John", "john.doe@univ-amu.fr", "P@ssw0rd", "5678 Another Street", "0987654321", "professor");
        userRepository.save(user);

        // Save new Ue
        Set<String> ueTags = new HashSet<>();
        ueTags.add("Math");
        ueTags.add("Science");
        ue = new Ue("Calculus", Informatique, Licence_3, List.of("SP1", "SP2"), user, ueTags);
        ueRepository.save(ue);

        // Save new Exercice
        Set<String> exerciceTags = new HashSet<>();
        exerciceTags.add("Algebra");
        exerciceTags.add("Geometry");
        exercise = new Exercise("Math Exercise", "Mathematics", "Solve the equation", "x=2", ue, user, exerciceTags);
        exerciseRepository.save(exercise);
    }

    @Test
    void findById() {
        assertEquals(exercise.getTitle(), exerciseRepository.findById(exercise.getId()).get().getTitle());
    }

    @Test
    void findByTitleContaining() {
        List<Exercise> exercises = exerciseRepository.findByTitleContainingIgnoreCase("Math");
        assertFalse(exercises.isEmpty());
        assertEquals(exercise.getTitle(), exercises.get(0).getTitle());
    }

    @Test
    void findByCategory() {
        List<Exercise> exercises = exerciseRepository.findByCategory("Mathematics");
        assertFalse(exercises.isEmpty());
        assertEquals(exercise.getCategory(), exercises.get(0).getCategory());
    }

    @Test
    void findByUe() {
        List<Exercise> exercises = exerciseRepository.findByUe(ue);
        assertFalse(exercises.isEmpty());
        assertEquals(exercise.getTitle(), exercises.get(0).getTitle());
    }

    @Test
    void findByCreator() {
        List<Exercise> exercises = exerciseRepository.findByCreator(user);
        assertFalse(exercises.isEmpty());
        assertEquals(exercise.getTitle(), exercises.get(0).getTitle());
    }

    @Test
    void findByUeId() {
        List<Exercise> exercises = exerciseRepository.findByUeId(ue.getId());
        assertFalse(exercises.isEmpty());
        assertEquals(exercise.getTitle(), exercises.get(0).getTitle());
    }

    @Test
    void findByCreatorId() {
        List<Exercise> exercises = exerciseRepository.findByCreatorId(user.getId());
        assertFalse(exercises.isEmpty());
        assertEquals(exercise.getTitle(), exercises.get(0).getTitle());
    }

    @Test
    void findByTagsContaining() {
        List<Exercise> exercises = exerciseRepository.findByTagsContainingIgnoreCase("Algebra");
        assertFalse(exercises.isEmpty());
        assertTrue(exercises.get(0).getTags().contains("Algebra"));

        exercises = exerciseRepository.findByTagsContainingIgnoreCase("Geometry");
        assertFalse(exercises.isEmpty());
        assertTrue(exercises.get(0).getTags().contains("Geometry"));
    }
}