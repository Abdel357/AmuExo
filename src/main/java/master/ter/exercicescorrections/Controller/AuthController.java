package master.ter.exercicescorrections.Controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import master.ter.exercicescorrections.Service.EmailService;
import master.ter.exercicescorrections.model.User;
import master.ter.exercicescorrections.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    @PreAuthorize("isAnonymous()")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    @PreAuthorize("isAnonymous()")
    public String processSignup(@Valid User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Cet email est déjà utilisé.");
            return "signup";
        }
        user.setRole("Etudiant");
        user.addRole("Etudiant");
        user.setPassword(encoder.encode(user.getPassword()));

        // Generate confirmation token
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setEnabled(false);

        userRepository.save(user);

        String confirmationUrl = "http://localhost:8080/confirm-account?token=" + user.getConfirmationToken();
        try {
            emailService.sendConfirmationEmail(user.getEmail(), "Confirmation d'inscription", confirmationUrl);
        } catch (MessagingException e) {
            logger.error("Error sending confirmation email", e);
        }

        logger.info("User registered -----> : {}", user);
        redirectAttributes.addFlashAttribute("message", "Un email de confirmation a été envoyé. Veuillez vérifier votre boîte de réception.");
        return "redirect:/login";
    }

    @GetMapping("/confirm-account")
    @PreAuthorize("isAnonymous()")
    public String confirmUserAccount(@RequestParam("token") String confirmationToken, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByConfirmationToken(confirmationToken);
        if (user != null) {
            user.setEnabled(true);
            user.setConfirmationToken(null);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "Votre compte a été confirmé avec succès.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Le lien de confirmation est invalide.");
            return "redirect:/signup";
        }
    }
}
