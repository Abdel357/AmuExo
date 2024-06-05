package master.ter.exercicescorrections.Controller;


import master.ter.exercicescorrections.model.*;
import master.ter.exercicescorrections.repository.ExerciseRepository;
import master.ter.exercicescorrections.repository.QuizzRepository;
import master.ter.exercicescorrections.repository.UeRepository;
import master.ter.exercicescorrections.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserRepository userRepository;
    private final UeRepository ueRepository;
    private final ExerciseRepository exerciseRepository;
    private final QuizzRepository quizzRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);


    @Autowired
    public StudentController(UserRepository userRepository, UeRepository ueRepository, ExerciseRepository exerciseRepository, QuizzRepository quizzRepository) {
        this.userRepository = userRepository;
        this.ueRepository = ueRepository;
        this.exerciseRepository = exerciseRepository;
        this.quizzRepository = quizzRepository;
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Etudiant')")
    public String getUserPage(@PathVariable("id") Long userId,Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)){
            return "error";
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.get().getRole().equals("Etudiant")){
            return "error";
        }
        List<Ue> userUes = ueRepository.findAll();
        model.addAttribute("allUe", userUes);
        model.addAttribute("id", userId);
        return "allUestudent";
    }

    @GetMapping("/{id}/search")
    @PreAuthorize("isAuthenticated()")
    public String searchAsStudent(@RequestParam String query, @PathVariable("id") Long userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)){
            return "error";
        }

        // Convertir la requête en minuscules
        String lowerCaseQuery = query == null ? "*" : query.toLowerCase();

        Set<Exercise> exercises = new HashSet<>();
        exercises.addAll(exerciseRepository.findByTitleContainingIgnoreCase(lowerCaseQuery));
        exercises.addAll(exerciseRepository.findByTagsContainingIgnoreCase(lowerCaseQuery));

        Set<Ue> ues = new HashSet<>();
        ues.addAll(ueRepository.findByTagsContainingIgnoreCase(lowerCaseQuery));
        ues.addAll(ueRepository.findByTitleContainingIgnoreCase(lowerCaseQuery));

        Set<Quizz> quizzes = new HashSet<>();
        quizzes.addAll(quizzRepository.findByTitleContainingIgnoreCase(lowerCaseQuery));
        quizzes.addAll(quizzRepository.findByTagsContainingIgnoreCase(lowerCaseQuery));

        Set<User> users = new HashSet<>(userRepository.findByLastNameContainingIgnoreCaseAndRole(lowerCaseQuery, "Enseignant"));

        model.addAttribute("exercises", exercises);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("ues", ues);
        model.addAttribute("users", users);


        return "searchResults_student";
    }

    @GetMapping("/{id}/userDetails")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Etudiant')")

    public ModelAndView getUserDetails(@PathVariable("id") Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User student = userRepository.findByEmail(userDetails.getUsername());
        ModelAndView modelAndView;
        if (!student.getId().equals(userId)){
            modelAndView = new ModelAndView("error");

        }
        else {
            modelAndView = new ModelAndView("userDetails");
            Optional<User> userOptional = userRepository.findById(userId);
            User user = new User();
            if (userOptional.isPresent()) {
                user = userOptional.get();
            }

            modelAndView.addObject("user", user);
        }
        return modelAndView;
    }

    @GetMapping("/{id}/ue")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Etudiant')")

    public String showAllUe(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(id)){
            return "error";
        }
        List<Ue> userUes = ueRepository.findAll();
        model.addAttribute("allUe", userUes);
        model.addAttribute("id", id);
        return "allUestudent";
    }

    @GetMapping("/{id}/ue/{ueId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Etudiant')")

    public String showUeDetails(@PathVariable Long id, @PathVariable Long ueId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(id)){
            return "error";
        }
        Optional<Ue> ueOptional = ueRepository.findById(ueId);

        if (ueOptional.isEmpty()) {
            return "redirect:/student/" + id + "/ue";
        }
        Ue ue = ueOptional.get();
        model.addAttribute("ue", ue);
        model.addAttribute("exercises", exerciseRepository.findByUeId(ueId));
        model.addAttribute("quizzes", quizzRepository.findByUeId(ueId));
        model.addAttribute("id", id);
        return "viewUestudent";
    }

    @GetMapping("/{id}/ue/{ueId}/exo/{exoId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Etudiant')")

    public String viewExercise(@PathVariable Long id, @PathVariable Long ueId,
                               @PathVariable Long exoId, Model model) {
        Exercise exercise = exerciseRepository.findById(exoId).orElse(null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User student = userRepository.findByEmail(userDetails.getUsername());

        if (!student.getId().equals(id)){
            return "error";
        }
        if (exercise == null) {
            return "redirect:/student/" + id + "/ue" + ueId;
        }
        model.addAttribute("exercise", exercise);
        model.addAttribute("id", id);

        // Ajouter l'utilisateur au modèle
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> model.addAttribute("user", user));

        return "viewExercicestudent";
    }

    @GetMapping("/{id}/ue/{ueId}/quizz/{quizzId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Etudiant')")

    public String viewQuizz(@PathVariable Long id, @PathVariable Long ueId,
                            @PathVariable Long quizzId, Model model) {
        Quizz quizz = quizzRepository.findById(quizzId).orElse(null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User student = userRepository.findByEmail(userDetails.getUsername());

        if (!student.getId().equals(id)){
            return "error";
        }
        if (quizz == null) {
            return "redirect:/student/" + id + "/ue" + ueId;
        }
        model.addAttribute("quizz", quizz);
        model.addAttribute("id", id);

        // Ajouter l'utilisateur au modèle
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> model.addAttribute("user", user));

        return "viewQuizz_student";
    }


    @PostMapping("{id}/ue/{ueId}/quizz/{quizzId}/submit")

    public String submitQuizz(@PathVariable Long id, @PathVariable Long ueId, @PathVariable Long quizzId,
                              @RequestParam Map<String, String> allParams, Model model) {
        Quizz quizz = quizzRepository.findById(quizzId).orElse(null);
        if (quizz == null) return "redirect:/student/" + id + "/ue" + ueId;

        int totalQuestions = quizz.getQuestions().size();
        int correctAnswers = 0;

        Map<Question, List<Answer>> correctAnswersMap = new HashMap<>();
        Map<Question, List<Answer>> userAnswersMap = new HashMap<>();
        Map<Question, Boolean> questionCorrectnessMap = new HashMap<>();

        for (Question question : quizz.getQuestions()) {
            List<Answer> correctAnswersForQuestion = question.getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .toList();

            List<Answer> userAnswersForQuestion = new ArrayList<>();
            boolean questionCorrect = true;

            for (Answer answer : question.getAnswers()) {
                String paramName = "question_" + question.getId() + "_" + answer.getId();
                boolean userAnswer = allParams.containsKey(paramName);

                if (userAnswer) {
                    userAnswersForQuestion.add(answer);
                }

                if (userAnswer != answer.isCorrect()) {
                    questionCorrect = false;
                }
            }

            if (questionCorrect) {
                correctAnswers++;
            }

            questionCorrectnessMap.put(question, questionCorrect);
            correctAnswersMap.put(question, correctAnswersForQuestion);
            userAnswersMap.put(question, userAnswersForQuestion);
        }

        double score = (double) correctAnswers / totalQuestions * 100;

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Mettre à jour la Map de scores du quizz
            if (user.getQuizzScores() == null) {
                user.setQuizzScores(new HashMap<>());
            }
            user.getQuizzScores().put(quizz, score);

            // Enregistrer les modifications dans la base de données
            userRepository.save(user);
        }

        model.addAttribute("score", score);
        model.addAttribute("quizz", quizz);
        model.addAttribute("ueId", ueId);
        model.addAttribute("userId", id);
        model.addAttribute("correctAnswersMap", correctAnswersMap);
        model.addAttribute("userAnswersMap", userAnswersMap);
        model.addAttribute("questionCorrectnessMap", questionCorrectnessMap);

        return "resultQuizz_student";
    }


}
