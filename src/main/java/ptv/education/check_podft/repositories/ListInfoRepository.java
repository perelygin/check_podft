package ptv.education.check_podft.repositories;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import ptv.education.check_podft.model.ListInfo;
import ptv.education.check_podft.model.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface ListInfoRepository extends JpaRepository<ListInfo,Integer> {
    @NativeQuery(value = "Select * FROM podft.list_info linf ORDER BY linf.list_date DESC LIMIT 1")
    ListInfo findLastListDate();
}
