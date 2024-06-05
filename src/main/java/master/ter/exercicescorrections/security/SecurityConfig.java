package master.ter.exercicescorrections.security;

import jakarta.servlet.DispatcherType;
import master.ter.exercicescorrections.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider myAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] anonymousRequests = { "/", "/signup", "/login"};
        String[] userRequests = { "/ue", "/ue/**", "/exercise", "/exercise/**", "/user", "/user/**", "/student", "/student/**" };
        String[] adminRequests = { };

        http.authorizeHttpRequests(config -> {
            config.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();
            config.dispatcherTypeMatchers(DispatcherType.REQUEST).permitAll();
            config.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();

            config.requestMatchers(anonymousRequests).permitAll();
            config.requestMatchers(userRequests).hasAnyAuthority("Etudiant", "Enseignant");
            config.requestMatchers(adminRequests).hasAnyAuthority("ADMIN");
            config.anyRequest().authenticated();
        });

        http.formLogin(config -> {
            config.loginPage("/login")
                    .loginProcessingUrl("/process-login")
                    .successHandler(authenticationSuccessHandler())
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .permitAll();
        });

        http.logout(config -> {
            config.logoutSuccessUrl("/");
            config.invalidateHttpSession(true);
            config.deleteCookies("JSESSIONID");
            config.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
            config.permitAll();
        });

        http.csrf(config -> {
            config.ignoringRequestMatchers(anonymousRequests);
        });

        return http.build();
    }
}
