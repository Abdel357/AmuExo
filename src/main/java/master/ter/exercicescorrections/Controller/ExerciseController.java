package master.ter.exercicescorrections.Controller;

import jakarta.validation.Valid;
import master.ter.exercicescorrections.Controller.formObject.ExerciseForm;
import master.ter.exercicescorrections.model.Exercise;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import master.ter.exercicescorrections.repository.ExerciseRepository;
import master.ter.exercicescorrections.repository.UeRepository;
import master.ter.exercicescorrections.repository.UserRepository;
import master.ter.exercicescorrections.util.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/user/{id}/exercise")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final UeRepository ueRepository;
    private final UserRepository userRepository;

    @Autowired
    public ExerciseController(ExerciseRepository exerciseRepository, UeRepository ueRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.ueRepository = ueRepository;
        this.userRepository = userRepository;
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

        model.addAttribute("exerciseForm", new ExerciseForm());
        model.addAttribute("allUes", ueRepository.findAll());
        model.addAttribute("userId", userId);
        model.addAttribute("ueId", ueId);
        model.addAttribute("allSubParts", ue.get().getSubParts());
        model.addAttribute("isEdit", false); // Indicateur pour le mode création
        return "createExercice";
    }

    @PostMapping("/{ueId}/create")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String createExercice(@PathVariable("id") Long userId, @PathVariable Long ueId, @ModelAttribute("exerciseForm") @Valid ExerciseForm exerciseForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUes", ueRepository.findAll());
            model.addAttribute("userId", userId);
            model.addAttribute("ueId", ueId);
            return "createExercice";
        }

        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Ue> ueOptional = ueRepository.findById(ueId);

        if (userOptional.isPresent() && ueOptional.isPresent()) {
            User user = userOptional.get();
            Ue ue = ueOptional.get();

            // Convertir les chaînes en collections
            Set<String> tags = new HashSet<>(Arrays.asList(exerciseForm.getTagsAsString().split("\\s*;\\s*")));

            // Créer un nouvel objet Exercise en utilisant ExerciseForm
            Exercise exercise = new Exercise();
            exercise.setTitle(exerciseForm.getTitle());
            exercise.setCategory(exerciseForm.getCategory());
            exercise.setTags(tags);
            exercise.setUe(ue);
            exercise.setStatement(TextFormatter.formatText(exerciseForm.getStatement()));
            exercise.setAnswer(TextFormatter.formatText(exerciseForm.getAnswer()));
            exercise.setCreator(user);

            // Sauvegarder l'objet Exercise
            exerciseRepository.save(exercise);
        }

        return "redirect:/user/" + userId + "/ue/" + ueId;
    }


    @GetMapping("/{ueId}/view/{exoId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String viewExercice(@PathVariable("id") Long userId, @PathVariable Long ueId, @PathVariable("exoId") Long exoId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User teacher = userRepository.findByEmail(userDetails.getUsername());

        if (!teacher.getId().equals(userId)){
            return "error";
        }
        Exercise exercise = exerciseRepository.findById(exoId).orElse(null);
        if (exercise == null) {
            return "redirect:/user/" + userId + "/ue/" + ueId;
        }
        model.addAttribute("exercise", exercise);
        model.addAttribute("userId", userId);

        // Ajouter l'utilisateur au modèle
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.ifPresent(user -> model.addAttribute("user", user));

        return "viewExercice";
    }


    @GetMapping("/{ueId}/edit/{exoId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String showEditForm(@PathVariable("id") Long userId, @PathVariable Long ueId, @PathVariable("exoId") Long exoId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)){
            return "error";
        }
        Optional<Exercise> exerciseOptional = exerciseRepository.findById(exoId);
        if (exerciseOptional.isEmpty()) return "redirect:/user/{userId}/ue/{ueId}";
        if(!exerciseOptional.get().getCreator().getId().equals(userId)){return "error";}
        Exercise exercise = exerciseOptional.get();
        ExerciseForm exerciseForm = new ExerciseForm();
        exerciseForm.setTitle(exercise.getTitle());
        exerciseForm.setCategory(exercise.getCategory());
        exerciseForm.setTagsAsString(String.join("; ", exercise.getTags()));
        exerciseForm.setStatement(exercise.getStatement());
        exerciseForm.setAnswer(exercise.getAnswer());

        model.addAttribute("exerciseForm", exerciseForm);
        model.addAttribute("allUes", ueRepository.findAll());
        model.addAttribute("userId", userId);
        model.addAttribute("allSubParts", exercise.getUe().getSubParts());
        model.addAttribute("ueId", ueId);
        model.addAttribute("exerciseId", exoId);
        model.addAttribute("isEdit", true); // Indicateur pour le mode édition
        return "editExercise";
    }

    @PostMapping("/{ueId}/edit/{exoId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String updateExercice(@PathVariable("id") Long userId, @PathVariable Long ueId, @PathVariable("exoId") Long exoId, @ModelAttribute("exerciseForm") @Valid ExerciseForm exerciseForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUes", ueRepository.findAll());
            model.addAttribute("userId", userId);
            model.addAttribute("ueId", ueId);
            model.addAttribute("exerciseId", exoId);
            return "editExercise";
        }

        Optional<Exercise> exerciseOptional = exerciseRepository.findById(exoId);
        if (exerciseOptional.isPresent()) {
            Exercise exercise = exerciseOptional.get();

            // Mettre à jour les propriétés de l'exercice avec les valeurs du formulaire
            exercise.setTitle(exerciseForm.getTitle());
            exercise.setCategory(exerciseForm.getCategory());
            exercise.setTags(new HashSet<>(Arrays.asList(exerciseForm.getTagsAsString().split("\\s*;\\s*"))));
            exercise.setStatement(TextFormatter.formatText(exerciseForm.getStatement()));
            exercise.setAnswer(TextFormatter.formatText(exerciseForm.getAnswer()));

            // Sauvegarder les modifications
            exerciseRepository.save(exercise);
        }

        return "redirect:/user/" + userId + "/ue/" + ueId;
    }

    @PostMapping("/{ueId}/delete/{exoId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String deleteExercise(@PathVariable("id") Long userId, @PathVariable Long ueId, @PathVariable("exoId") Long exoId) {
        exerciseRepository.deleteById(exoId);
        return "redirect:/user/" + userId + "/ue/" + ueId;
    }

}