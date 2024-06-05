package master.ter.exercicescorrections.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercices")
@EntityListeners(AuditingEntityListener.class)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Titre de l'exercice
    @Column(name = "title", nullable = false)
    private String title;

    // Catégorie de l'exercice
    @Column(name = "category", nullable = false)
    private String category;


    // Enoncé de l'exercice
    @Column(name = "Statement", nullable = false, columnDefinition = "LONGTEXT")
    private String statement;


    // Réponse de l'exercice
    @Column(name = "answer", nullable = false, columnDefinition = "LONGTEXT")
    private String answer;


    // L'UE liée à l'exercice
    @ManyToOne
    @JoinColumn(name = "id_ue", nullable = false)
    private Ue ue;

    // Créateur de l'exercice
    @ManyToOne
    @JoinColumn(name = "id_creator", nullable = false)
    private User creator;

    // Tags de l'exercice
    @ElementCollection
    private Set<String> tags;

    public Exercise(String title, String category, String statement, String answer, Ue ue, User creator, Set<String> tags) {
        this.title = title;
        this.category = category;
        this.statement = statement;
        this.answer = answer;
        this.ue = ue;
        this.creator = creator;
        this.tags = tags;
    }
}
