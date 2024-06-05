package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.Exercise;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByTitleContainingIgnoreCase(String title);

    List<Exercise> findByCategory(String category);

    List<Exercise> findByUeId(Long ueId);

    List<Exercise> findByCreatorId(Long creatorId);

    List<Exercise> findByUe(Ue ue);

    List<Exercise> findByCreator(User creator);

    List<Exercise> findByTagsContainingIgnoreCase(String tag);

    List<Exercise> findByUeIdAndCategory(Long ueId, String subPart);
}