package master.ter.exercicescorrections.Controller;

import jakarta.mail.MessagingException;
import master.ter.exercicescorrections.model.User;
import master.ter.exercicescorrections.repository.UserRepository;
import master.ter.exercicescorrections.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }


    @PostMapping("/forgot-password")
    public String forgotPasswordPost(@RequestParam("email") String email, RedirectAttributes redirectAttributes) throws MessagingException {
        String token = UUID.randomUUID().toString(); // Générer un vrai token ici
        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetToken(token);
            userRepository.save(user);

            emailService.sendResetPasswordEmail(email, "Réinitialisation de mot de passe", resetLink);
        }

        redirectAttributes.addFlashAttribute("message", "Un email de réinitialisation de mot de passe a été envoyé.");
        return "redirect:/login";
    }



    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            model.addAttribute("error", "Token invalide.");
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordPost(@RequestParam("token") String token,
                                    @RequestParam("newPassword") String newPassword,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    Model model, RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        User user = userRepository.findByResetToken(token);
        if (user == null) {
            model.addAttribute("error", "Token invalide.");
            return "reset-password";
        }

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Votre mot de passe a été réinitialisé avec succès.");
        return "redirect:/login";
    }
}

