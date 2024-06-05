package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.Quizz;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuizzRepository extends JpaRepository<Quizz, Long> {

    List<Quizz> findByTitleContainingIgnoreCase(String title);

    List<Quizz> findByCategory(String category);

    List<Quizz> findByCreatorId(Long creatorId);

    List<Quizz> findByUe(Ue ue);

    List<Quizz> findByUeId(Long ueId);

    List<Quizz> findByCreator(User creator);

    List<Quizz> findByTagsContainingIgnoreCase(String tag);

    List<Quizz> findByUeIdAndCategory(Long ueId, String subPart);
}