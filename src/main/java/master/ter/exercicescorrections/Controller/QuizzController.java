package master.ter.exercicescorrections.Controller;

import jakarta.validation.Valid;
import master.ter.exercicescorrections.Controller.formObject.AnswerForm;
import master.ter.exercicescorrections.Controller.formObject.ExerciseForm;
import master.ter.exercicescorrections.Controller.formObject.QuestionForm;
import master.ter.exercicescorrections.Controller.formObject.QuizzForm;
import master.ter.exercicescorrections.model.*;
import master.ter.exercicescorrections.repository.*;
import master.ter.exercicescorrections.util.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/{id}/quizz")
public class QuizzController {


    private final QuizzRepository quizzRepository;
    private final UeRepository ueRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuizzController(QuizzRepository quizzRepository, UeRepository ueRepository, UserRepository userRepository, QuestionRepository questionRepository) {
        this.quizzRepository = quizzRepository;
        this.ueRepository = ueRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    @GetMapping("/{ueId}/create")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String showCreateForm(@PathVariable("id") Long userId, @PathVariable Long ueId, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)){
            return "error";
        }
        Optional<Ue> ue = ueRepository.findById(ueId);
        if (ue.isEmpty()) return "redirect:/user/{userId}/ue";

        model.addAttribute("quizzForm", new QuizzForm());
        model.addAttribute("allUes", ueRepository.findAll());
        model.addAttribute("userId", userId);
        model.addAttribute("ueId", ueId);
        model.addAttribute("allSubParts", ue.get().getSubParts());
        model.addAttribute("isEdit", false); // Indicateur pour le mode création
        return "createQuizz";
    }

    @PostMapping("/{ueId}/create")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String createQuizz(@PathVariable("id") Long userId, @PathVariable Long ueId, @ModelAttribute("quizzForm") @Valid QuizzForm quizzForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUes", ueRepository.findAll());
            model.addAttribute("userId", userId);
            model.addAttribute("ueId", ueId);
            return "createQuizz";
        }

        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Ue> ueOptional = ueRepository.findById(ueId);

        if (userOptional.isPresent() && ueOptional.isPresent()) {
            User user = userOptional.get();
            Ue ue = ueOptional.get();

            // Convertir les chaînes en collections
            Set<String> tags = new HashSet<>(Arrays.asList(quizzForm.getTagsAsString().split("\\s*;\\s*")));

            Quizz quizz = new Quizz();
            quizz.setTitle(quizzForm.getTitle());
            quizz.setCategory(quizzForm.getCategory());
            quizz.setUe(ue);
            quizz.setTags(tags);


            Quizz savedQuizz = quizzRepository.save(quizz);

            List<Question> questions = quizzForm.getQuestions();
            for (Question question : questions) {
                question.setQuizz(savedQuizz);
            }
            questionRepository.saveAll(questions);
        }
        return "redirect:/user/" + userId + "/ue/" + ueId;
    }

    @GetMapping("/{ueId}/view/{quizzId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String viewQuizz(@PathVariable("id") Long userId, @PathVariable Long ueId, @PathVariable("quizzId") Long quizzId, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User teacher = userRepository.findByEmail(userDetails.getUsername());

        if (!teacher.getId().equals(userId)){
            return "error";
        }
        Quizz quizz = quizzRepository.findById(quizzId).orElse(null);
        if (quizz == null) {
            return "redirect:/user/" + userId + "/ue/" + ueId;
        }
        model.addAttribute("quizz", quizz);
        model.addAttribute("userId", userId);

        // Ajouter l'utilisateur au modèle
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.ifPresent(user -> model.addAttribute("user", user));

        return "viewQuizz";
    }


    @PostMapping("/{ueId}/delete/{quizzId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String deleteQuizz(@PathVariable("id") Long userId, @PathVariable Long ueId, @PathVariable("quizzId") Long quizzId) {
        Optional<Quizz> quizz = quizzRepository.findById(quizzId);

        if (quizz.isPresent()) {
            for (User user : userRepository.findByRole("Etudiant")) {
                user.getQuizzScores().remove(quizz.get());
            }
        }

        quizzRepository.deleteById(quizzId);
        return "redirect:/user/" + userId + "/ue/" + ueId;
    }


}