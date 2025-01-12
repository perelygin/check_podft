package ptv.education.check_podft.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptv.education.check_podft.model.Subject;
import ptv.education.check_podft.repositories.SubjectsRepository;
import ptv.education.check_podft.util.SubjectNotFoundExeption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SubjectsService {


    private final SubjectsRepository subjectsRepository;
    @Autowired
    public SubjectsService(SubjectsRepository subjectsRepository) {
        this.subjectsRepository = subjectsRepository;
    }

    public List<Subject> findAll(){
        return subjectsRepository.findAll();
    }

    public Subject findOne(int id){
        Optional<Subject> foundSubject = subjectsRepository.findById(id);
        return  foundSubject.orElse(null);
    }
    /*
         Поиск терорриста по ФИО
     */
    public Optional<Subject> findBySubjectFioAndSubjectBirthday(String subjectFio,LocalDate birthday){
        Optional<Subject> subject = subjectsRepository.findBySubjectFioAndSubjectBirthday(subjectFio.toUpperCase(), birthday);  //Приводим ФИО к верхнему регистру
        return subject;
    }
    public Optional<Subject> findBySubjectInn(String inn){
        Optional<Subject> subject = subjectsRepository.findBySubjectInn(inn);
        return subject;
        //return subject.orElseThrow(SubjectNotFoundExeption::new);
    }
    public Optional<Subject> findBySubjectSnils(String snils){
        Optional<Subject> subject = subjectsRepository.findBySubjectSnils(snils);
        return subject;
        //return subject.orElseThrow(SubjectNotFoundExeption::new);
    }

    @Transactional
    public void save(Subject subject){
        subjectsRepository.save(subject);
    }

    @Transactional
    public void update(int id, Subject updatedSubject){
        updatedSubject.setSubjectId(id);
        subjectsRepository.save(updatedSubject);
    }

    @Transactional
    public void truncateSubjects(){
        subjectsRepository.truncateSubjects();
        System.out.println("Subjects truncated");
    }
    @Transactional
    public void delete(int id){
        subjectsRepository.deleteById(id);
    }
}
