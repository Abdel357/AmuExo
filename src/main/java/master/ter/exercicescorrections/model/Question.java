package master.ter.exercicescorrections.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statement;

    @ManyToOne
    private Quizz quizz;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", statement='" + statement + '\'' +
                ", quizz=" + quizz +
                ", answers=" + answers +
                '}';
    }

    public Question(String statement, List<Answer> answers, Quizz quizz) {
        this.statement = statement;
        this.answers = answers;
        this.quizz = quizz;
    }
}
