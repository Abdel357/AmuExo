package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteByQuizzId(Long id);

    List<Question> findByQuizzId(Long id);
}