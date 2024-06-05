package master.ter.exercicescorrections.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ues")
@EntityListeners(AuditingEntityListener.class)

public class Ue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain", nullable = false)
    private Domain domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "yearUE", nullable = false)
    private AcademicYear year;

    @ElementCollection
    private List<String> subParts;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = true)
    private User creator;

    @ElementCollection
    private Set<String> tags;

    public Ue(String title, Domain domain, AcademicYear year, List<String> subParts, User creator, Set<String> tags) {
        this.title = title;
        this.domain = domain;
        this.year = year;
        this.subParts = subParts;
        this.creator = creator;
        this.tags = tags;
    }



    @Override
    public String toString() {
        return "Ue{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", domain=" + domain +
                ", year=" + year +
                ", subParts=" + subParts +
                ", creator=" + creator +
                ", tags=" + tags +
                '}';
    }
}
