package ptv.education.check_podft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ptv.education.check_podft.model.Subject;

@Repository
public interface DocumentsRepository extends JpaRepository<Subject,Integer> {
    @Modifying
    @Query(
            value = "truncate table podft.documents",
            nativeQuery = true
    )
    void truncateDocuments();
}
