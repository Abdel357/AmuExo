package master.ter.exercicescorrections.repository;

import master.ter.exercicescorrections.model.AcademicYear;
import master.ter.exercicescorrections.model.Domain;
import master.ter.exercicescorrections.model.Ue;
import master.ter.exercicescorrections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UeRepository extends JpaRepository<Ue, Long> {

    List<Ue> findByTitleContainingIgnoreCase(String title);

    List<Ue> findByDomain(Domain domain);

    List<Ue> findByYear(AcademicYear year);

    List<Ue> findByCreatorId(Long creatorId);

    List<Ue> findByTagsContainingIgnoreCase(String tag);

    List<Ue> findByCreator(User creator);

}