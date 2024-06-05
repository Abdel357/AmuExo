package master.ter.exercicescorrections.util;

import master.ter.exercicescorrections.model.*;
import master.ter.exercicescorrections.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UeRepository ueRepository;

    @Autowired
    private QuizzRepository quizzRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Génère des données initiales pour la base de données.
     */
    @PostConstruct
    public void generateData() {
        // Créer et sauvegarder des utilisateurs
        List<User> users = Arrays.asList(
                new User("Jacques", "Brel", "jacques@etu.univ-amu.fr", encoder.encode("Jacques123"), "123 Rue Exemple", "0102030405", "Etudiant"),
                new User("Jean", "Goldman", "jean@etu.univ-amu.fr", encoder.encode("Jean456789"), "456 Rue Exemple", "0607080910", "Etudiant"),
                new User("Francoise", "Hardi", "francoise@univ-amu.fr", encoder.encode("Hardi789"), "789 Rue Exemple", "1112131415", "Enseignant"),
                new User("Gilbert", "Becaud", "gilbert@univ-amu.fr", encoder.encode("Gilbert101"), "101 Rue Exemple", "1617181920", "Enseignant")
        );

        for (User user : users) {
            user.addRole(user.getRole());
        }

        userRepository.saveAll(users);

        // Créer et sauvegarder des unités d'enseignement (UE)
        List<Ue> ues = Arrays.asList(
                new Ue("Analyse", Domain.Mathematiques, AcademicYear.Licence_3, Arrays.asList("Séries et suites", "Équation quadratique"), users.get(2), new HashSet<>(Arrays.asList("maths", "science", "analyse", "série"))),
                new Ue("Force et Statique", Domain.Physique, AcademicYear.Licence_2, Arrays.asList("Notions de force", "Lois de Newton"), users.get(3), new HashSet<>(Arrays.asList("physiques", "science"))),
                new Ue("Introduction à l'Informatique", Domain.Informatique, AcademicYear.Licence_1, Arrays.asList("Algorithmique", "POO"), users.get(2), new HashSet<>(Arrays.asList("computer", "tech"))),
                new Ue("Rappel des fondamentaux de Chimie", Domain.Chimie, AcademicYear.Master_1, Arrays.asList("Atomes & molécules", "Réactions chimiques"), users.get(3), new HashSet<>(Arrays.asList("chemistry", "science")))
                );

        ueRepository.saveAll(ues);


        // Créer et sauvegarder des quizzes
        List<Quizz> quizzes = Arrays.asList(
                new Quizz("Quiz 1 Mathématiques", "Mathématiques", new HashSet<>(Arrays.asList("quiz", "maths")), null, ues.get(0), users.get(2)),
                new Quizz("Quiz 2 Physique", "Physique", new HashSet<>(Arrays.asList("quiz", "physics")), null, ues.get(1), users.get(3)),
                new Quizz("Quiz 3 Informatique", "Informatique", new HashSet<>(Arrays.asList("quiz", "computer")), null, ues.get(2), users.get(2)),
                new Quizz("Quiz 4 Chimie", "Chimie", new HashSet<>(Arrays.asList("quiz", "chemistry")), null, ues.get(3), users.get(3))
        );

        quizzRepository.saveAll(quizzes);

        List<Question> questions = Arrays.asList(
                new Question("Quelle(s) est(sont) la(les) valeur(s) de x pour : x² − 5x + 6 = 0",
                        Arrays.asList(
                                new Answer("x = 1", false),
                                new Answer("x = 2", true),
                                new Answer("x = 3", true)
                        ), quizzes.get(0)),
                new Question("Quelle(s) est(sont) la(les) valeur(s) de x pour : x² - 4x = -4",
                        Arrays.asList(
                                new Answer("x = 2", true),
                                new Answer("x = 4", false),
                                new Answer("x = 0", false)
                        ), quizzes.get(0)),

                new Question("Calculer la force exercée par un objet de 10 kg en chute libre, sachant que l'accélération due à la gravité est de 9.8m/s²",
                        Arrays.asList(
                                new Answer("980N", false),
                                new Answer("98N", true),
                                new Answer("Pas de force", false)
                        ), quizzes.get(1)),

                new Question("Quelle fonction Python retourne la factorielle d'un nombre donné ?",
                        Arrays.asList(
                                new Answer("def factorial(n):\n" +
                                        "    if n == 0:\n" +
                                        "        return 1\n" +
                                        "    else:\n" +
                                        "        return n * factorial(n-1)\n", true),
                                new Answer("int factorial(int n){\n" +
                                        "    if (n == 0) return 1;\n" +
                                        "    else return n * factorial(n-1);}\n", false)
                        ), quizzes.get(2)),

                new Question("Quelle est la formule chimique du méthane",
                        Arrays.asList(
                                new Answer("CH4", true),
                                new Answer("CH3", false),
                                new Answer("C4H", false)
                        ), quizzes.get(3)),

                new Question("Vrai ou faux, la formule du propane est C3H8 ?",
                        Arrays.asList(
                                new Answer("Vrai", true),
                                new Answer("Faux", false)
                        ), quizzes.get(3))
        );
        questionRepository.saveAll(questions);


        // Créer et sauvegarder des exercices
        List<Exercise> exercises = Arrays.asList(
                new Exercise("Exercice 1", "Équation quadratique", "Résoudre l'équation quadratique suivante :   x² − 5x + 6 = 0", "x = 2 et  x = 3 ", ues.get(0), users.get(2), new HashSet<>(Arrays.asList("exercise", "maths"))),
                new Exercise("Exercice 2", "Notions de force", "Calculer la force exercée par un objet de 10 kg en chute libre, sachant que l'accélération due à la gravité est de 9.8m/s²", "La force exercée est F=m*g= 10kg * 9.8m/s² = 98N.", ues.get(1), users.get(3), new HashSet<>(Arrays.asList("exercise", "physics"))),
                new Exercise("Exercice 3", "Algorithmique", "Écrire une fonction en Python qui retourne la factorielle d'un nombre donné.", "def factorial(n):\n" +
                        "    if n == 0:\n" +
                        "        return 1\n" +
                        "    else:\n" +
                        "        return n * factorial(n-1)\n", ues.get(2), users.get(2), new HashSet<>(Arrays.asList("exercise", "computer"))),
                new Exercise("Exercice 4", "Atomes & molécules", "Écrire la formule chimique du méthane et expliquer sa structure moléculaire.", " La formule chimique du méthane est CH4. Le méthane est constitué d'un atome de carbone central lié à quatre atomes d'hydrogène par des liaisons covalentes simples, formant une structure tétraédrique.", ues.get(3), users.get(3), new HashSet<>(Arrays.asList("exercise", "chemistry")))
        );

        exerciseRepository.saveAll(exercises);
    }
}
