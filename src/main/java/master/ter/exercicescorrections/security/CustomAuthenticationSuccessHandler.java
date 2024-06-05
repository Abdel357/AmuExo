


package master.ter.exercicescorrections.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import master.ter.exercicescorrections.Service.CustomUser;
import master.ter.exercicescorrections.Service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Authentication successful for user: {}", authentication);
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            request.getSession().setAttribute("user", customUser);
            if (customUser.getUser().getRole().equals("Enseignant")) {
                response.sendRedirect("/user/" + customUser.getId()+"/ue");
            }
            else {
                response.sendRedirect("/student/" + customUser.getId()+"/ue");
            }

        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}
