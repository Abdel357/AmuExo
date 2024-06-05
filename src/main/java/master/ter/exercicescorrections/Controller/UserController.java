
package master.ter.exercicescorrections.Controller;

import master.ter.exercicescorrections.model.Exercise;
import master.ter.exercicescorrections.model.Quizz;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import master.ter.exercicescorrections.repository.ExerciseRepository;
import master.ter.exercicescorrections.repository.QuizzRepository;
import master.ter.exercicescorrections.repository.UeRepository;
import master.ter.exercicescorrections.repository.UserRepository;
import master.ter.exercicescorrections.Service.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {


    private final UserRepository userRepository;
    private final UeRepository ueRepository;
    private final ExerciseRepository exerciseRepository;
    private final QuizzRepository quizzRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserController(UserRepository userRepository, UeRepository ueRepository, ExerciseRepository exerciseRepository, QuizzRepository quizzRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.ueRepository = ueRepository;
        this.exerciseRepository = exerciseRepository;
        this.quizzRepository = quizzRepository;
        this.encoder = encoder;
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")
    public ModelAndView getUserPage(@PathVariable("id") Long userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User teacher = userRepository.findByEmail(userDetails.getUsername());
        ModelAndView modelAndView;
        Optional<User> userOptional = userRepository.findById(userId);

        if (!teacher.getId().equals(userId)){
            modelAndView = new ModelAndView("error");

        } else if (!userOptional.get().getRole().equals("Enseignant")) {
            modelAndView = new ModelAndView("error");
        }

       else {
            modelAndView = new ModelAndView("user");
            userOptional.ifPresent(user -> modelAndView.addObject("user", user));
        }
        return modelAndView;
    }

    @GetMapping("/{id}/userDetails")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")
    public ModelAndView getUserDetails(@PathVariable("id") Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User teacher = userRepository.findByEmail(userDetails.getUsername());
        ModelAndView modelAndView;
        if (!teacher.getId().equals(userId)){
            modelAndView = new ModelAndView("error");

        }
        else {
            modelAndView = new ModelAndView("userDetails");
            Optional<User> userOptional = userRepository.findById(userId);
            User user = new User();
            if (userOptional.isPresent()) {
                user = userOptional.get();
                modelAndView.addObject("user", user);
                List<Ue> ues = ueRepository.findByCreatorId(userId);
                modelAndView.addObject("ues", ues);
            }
        }

        return modelAndView;
    }

    @GetMapping("/{id}/creatorDetails")
    public ModelAndView getCreatorDetails(@PathVariable("id") Long userId) {
        ModelAndView modelAndView = new ModelAndView("creatorDetails");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            modelAndView.addObject("user", user);
            List<Ue> ues = ueRepository.findByCreatorId(userId);
            modelAndView.addObject("ues", ues);
        }
        return modelAndView;
    }

    @PostMapping("/updatePassword")
    @PreAuthorize("isAuthenticated()")

    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();

        if (!encoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Le mot de passe actuel est incorrect.");
            return "userDetails";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Les nouveaux mots de passe ne correspondent pas.");
            return "userDetails";
        }
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès.");
        return "redirect:/user/" + user.getId() + "/userDetails";
    }

    @GetMapping("/{id}/search")
    @PreAuthorize("isAuthenticated() && @securityService.hasRole(authentication, 'Enseignant')")

    public String searchAsTeacher(@RequestParam String query, @PathVariable("id") Long userId, Model model) {
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
        exercises.removeIf(exercise -> (!exercise.getCreator().getId().equals(userId)));
        Set<Ue> ues = new HashSet<>();

        ues.addAll(ueRepository.findByTagsContainingIgnoreCase(lowerCaseQuery));
        ues.addAll(ueRepository.findByTitleContainingIgnoreCase(lowerCaseQuery));
        ues.removeIf(ue -> (!ue.getCreator().getId().equals(userId)));


        Set<Quizz> quizzes = new HashSet<>();
        quizzes.addAll(quizzRepository.findByTitleContainingIgnoreCase(lowerCaseQuery));
        quizzes.addAll(quizzRepository.findByTagsContainingIgnoreCase(lowerCaseQuery));
        quizzes.removeIf(quizz -> (!quizz.getCreator().getId().equals(userId)));

        model.addAttribute("exercises", exercises);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("ues", ues);

        return "searchResults";
    }
}
