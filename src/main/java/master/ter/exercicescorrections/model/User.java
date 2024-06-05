package master.ter.exercicescorrections.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import master.ter.exercicescorrections.Service.EmailDomain;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    /**
     * Identifiant du user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nom du user.
     */

    @NotBlank(message = "Nom ne peut pas être vide")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Prénom du user.
     */
    @NotBlank(message = "Prénom ne peut pas être vide")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Email du user.
     */

    @NotBlank(message = "Email ne peut pas être vide")
    //@Email(message = "Format de l'email invalide")
    @EmailDomain
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /**
     * Mot de passe du user.
     */
    @NotBlank(message = "Mot de passe ne peut pas être vide")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message = "Le mot de passe doit contenir au moins une lettre majuscule, une lettre minuscule, un chiffre et avoir une longueur minimale de 8 caractères.")    @Column(name = "password", nullable = false)
    private String password;

    /**
     * l'adresse du user.
     */
    @NotBlank(message = "address ne peut pas être vide")
    @Column(name = "adress", nullable = true)
    private String adress;


    /**
     * numéro de téléphone du user.
     */
    @NotBlank(message = "numéro de téléphone ne peut pas être vide")
    @Column(name = "phone", nullable = true)
    private String phone;


    /**
     * Date de création du user.
     */
    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    /**
     * Date de dernière modification du user.
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;


    @Column(name = "role", nullable = false)
    private String role;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyJoinColumn(name = "quizz_id")
    @CollectionTable(name = "user_quizz_scores", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "score")
    private Map<Quizz, Double> quizzScores = new HashMap<>();


    @ElementCollection(fetch = FetchType.EAGER)
    Collection<String> authorities;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "enabled")
    private boolean enabled = false;

    public User(String lastName, String firstName, String email, String password, String adress, String phone, String role) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.adress = adress;
        this.phone = phone;
        this.role = role;
    }

    public void addRole(String role) {
        if (authorities == null)
            authorities = new HashSet<>();

        authorities.add(role);
    }

}
