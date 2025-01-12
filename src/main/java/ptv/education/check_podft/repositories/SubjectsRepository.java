package ptv.education.check_podft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ptv.education.check_podft.model.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectsRepository extends JpaRepository<Subject,Integer> {
    @Modifying
    @Query(
            value = "truncate table podft.subjects cascade",
            nativeQuery = true
    )
    void truncateSubjects();

    Optional<Subject> findBySubjectFioAndSubjectBirthday(String subjectFio, LocalDate birthday); //поиск террориста по ФИО+дата рождения
    Optional<Subject> findBySubjectInn(String inn);   //поиск террориста по ИНН
    Optional<Subject> findBySubjectSnils(String snils);  //поиск террориста по СНИЛС
}
