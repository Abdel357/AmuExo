package master.ter.exercicescorrections.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quizz")
@EntityListeners(AuditingEntityListener.class)
public class Quizz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "quizz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @ManyToOne
    @JoinColumn(name = "ue_id", nullable = false)
    private Ue ue;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = true)
    private User creator;

    @ElementCollection
    private Set<String> tags;

    public Quizz(String title, String category, Set<String> tags, List<Question> questions, Ue ue, User creator) {
        this.title = title;
        this.category = category;
        this.questions = questions;
        this.tags = tags;
        this.ue = ue;
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "Quizz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", ue=" + ue.getTitle() +
                ", tags=" + tags +
                '}';
    }


}
