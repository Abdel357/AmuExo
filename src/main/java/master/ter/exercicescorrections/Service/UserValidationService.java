package master.ter.exercicescorrections.Service;

import master.ter.exercicescorrections.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService {
    public String validateUser(User user, String confirmPassword) {
        if (!user.getPassword().equals(confirmPassword)) {
            return "Les mots de passe ne correspondent pas";
        }
        return "";
    }
}
