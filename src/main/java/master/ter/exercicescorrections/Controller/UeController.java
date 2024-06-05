package master.ter.exercicescorrections.Controller;

import jakarta.validation.Valid;
import master.ter.exercicescorrections.Controller.formObject.ExerciseForm;
import master.ter.exercicescorrections.Controller.formObject.UeForm;
import master.ter.exercicescorrections.model.Exercise;
import master.ter.exercicescorrections.model.Quizz;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import master.ter.exercicescorrections.repository.ExerciseRepository;
import master.ter.exercicescorrections.repository.QuizzRepository;
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
@RequestMapping("/user/{id}/ue")
public class UeController {


    private final UeRepository ueRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final QuizzRepository quizzRepository;

    @Autowired
    public UeController(UeRepository ueRepository, ExerciseRepository exerciseRepository, UserRepository userRepository, QuizzRepository quizzRepository) {
        this.ueRepository = ueRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.quizzRepository = quizzRepository;
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String showAllUe(@PathVariable("id") Long userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User teacher = userRepository.findByEmail(userDetails.getUsername());

        if (!teacher.getId().equals(userId)){
            return "error";
        }
        Optional<User> user= userRepository.findById(userId);
        if (!user.get().getRole().equals("Enseignant")){
            return "error";
        }
        List<Ue> userUes = ueRepository.findByCreatorId(userId);
        model.addAttribute("allUe", userUes);
        model.addAttribute("userId", userId);
        return "allUe";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String showUeForm(@PathVariable("id") Long userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)){
            return "error";
        }
        model.addAttribute("ueForm", new UeForm());
        model.addAttribute("userId", userId);
        return "formUe";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String createUe(@PathVariable("id") Long userId, @ModelAttribute("ueForm") @Valid UeForm ueForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userId);
            return "formUe";
        }

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Convertir les chaînes en collections
            List<String> subParts = Arrays.asList(ueForm.getSubPartsAsString().split("\\s*;\\s*"));
            Set<String> tags = new HashSet<>(Arrays.asList(ueForm.getTagsAsString().split("\\s*;\\s*")));

            // Créer un nouvel objet Ue en utilisant UeForm
            Ue ue = new Ue();
            ue.setTitle(ueForm.getTitle());
            ue.setDomain(ueForm.getDomain());
            ue.setYear(ueForm.getYear());
            ue.setSubParts(subParts);
            ue.setTags(tags);
            ue.setCreator(user);

            // Sauvegarder l'objet Ue
            ueRepository.save(ue);
        }

        return "redirect:/user/" + userId + "/ue"; // Redirige vers la page affichant toutes les UE de l'utilisateur
    }

    @GetMapping("/{ueId}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String showUeDetails(@PathVariable("id") Long userId, @PathVariable Long ueId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)){
            return "error";
        }

        Optional<Ue> ueOptional = ueRepository.findById(ueId);
        if (!ueOptional.isPresent()) {
            return "redirect:/user/" + userId + "/ue";
        }
        if(ueOptional.get().getCreator().getId().equals(userId)){
            Ue ue = ueOptional.get();
            model.addAttribute("ue", ue);
            model.addAttribute("exercises", exerciseRepository.findByUeId(ueId));
            model.addAttribute("quizzes", quizzRepository.findByUeId(ueId));
            model.addAttribute("userId", userId);
            return "viewUe";
        }
       return "error";
    }


    @GetMapping("/{ueId}/edit")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String showEditForm(@PathVariable("id") Long userId, @PathVariable Long ueId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)){
            return "error";
        }
        Optional<Ue> ueOptional = ueRepository.findById(ueId);
        if (ueOptional.isEmpty()) return "redirect:/user/" + userId + "/ue";
        if(!ueOptional.get().getCreator().getId().equals(userId)){return "error";}
        Ue ue = ueOptional.get();
        UeForm ueForm = new UeForm();
        ueForm.setTitle(ue.getTitle());
        ueForm.setDomain(ue.getDomain());
        ueForm.setYear(ue.getYear());
        ueForm.setSubPartsAsString(String.join("; ", ue.getSubParts()));
        ueForm.setTagsAsString(String.join("; ", ue.getTags()));


        model.addAttribute("ueForm", ueForm);
        model.addAttribute("userId", userId);
        model.addAttribute("ueId", ueId);
        model.addAttribute("oldSubParts", ueForm.getSubPartsAsString());


        return "editUe";
    }

    @PostMapping("/{ueId}/edit")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String updateUe(@PathVariable("id") Long userId, @PathVariable Long ueId, @RequestParam String oldSubParts, @ModelAttribute("ueForm") @Valid UeForm ueForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUes", ueRepository.findAll());
            model.addAttribute("userId", userId);
            model.addAttribute("ueId", ueId);
            return "editUe";
        }

        Optional<Ue> ueOptional = ueRepository.findById(ueId);
        if (ueOptional.isPresent()) {
            Ue ue = ueOptional.get();

            // Mettre à jour les propriétés de l'exercice avec les valeurs du formulaire
            ue.setTitle(ueForm.getTitle());
            List<String> tagsList = new ArrayList<>(Arrays.asList(ueForm.getTagsAsString().split("\\s*;\\s*")));
            ue.setTags(new HashSet<>(tagsList));


            String[] oldSubPartsList = oldSubParts.split("\\s*;\\s*");
            List<String> subPartsList = new ArrayList<>(Arrays.asList(ueForm.getSubPartsAsString().split("\\s*;\\s*")));
            ue.setSubParts(subPartsList);

            // Suppression des exercices associés à des sous-parties obsolètes
            List<Exercise> exercisesToDelete = new ArrayList<>();
            for (String subPart : oldSubPartsList) {
                if (!subPartsList.contains(subPart)) {
                    exercisesToDelete.addAll(exerciseRepository.findByUeIdAndCategory(ueId, subPart));
                }
            }
            exerciseRepository.deleteAll(exercisesToDelete);


            // Suppression des quiz associés à des sous-parties obsolètes
            List<Quizz> quizzesToDelete = new ArrayList<>();
            for (String subPart : oldSubPartsList) {
                if (!subPartsList.contains(subPart)) {
                    quizzesToDelete.addAll(quizzRepository.findByUeIdAndCategory(ueId, subPart));
                    for (Quizz quizz : quizzesToDelete) {
                        for (User user : userRepository.findByRole("Etudiant")) {
                            user.getQuizzScores().remove(quizz);
                        }
                    }
                }
            }
            quizzRepository.deleteAll(quizzesToDelete);

            // Sauvegarder les modifications
            ueRepository.save(ue);
        }

        return "redirect:/user/" + userId + "/ue";
    }

    @PostMapping("/{ueId}/delete")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String deleteUe(@PathVariable("id") Long userId, @PathVariable Long ueId) {
        List<Quizz> quizzes = quizzRepository.findByUeId(ueId);
        List<Exercise> exercises = exerciseRepository.findByUeId(ueId);

        for (Quizz quizz : quizzes) {
            for (User user : userRepository.findByRole("Etudiant")) {
                user.getQuizzScores().remove(quizz);
            }
        }

        quizzRepository.deleteAll(quizzes);
        exerciseRepository.deleteAll(exercises);

        ueRepository.deleteById(ueId);
        return "redirect:/user/" + userId + "/ue";
    }
}
