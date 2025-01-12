package ptv.education.check_podft.dto;

import org.hibernate.validator.constraints.NotEmpty;
import ptv.education.check_podft.model.Document;

import java.time.LocalDate;
import java.util.List;

public class SubjectDTO {

    private String subjectInn;
    private String subjectSnils;
    @NotEmpty(message="FIO cann't be empty")
    private String subjectFio;
    private LocalDate subjectBirthday;
    private List<Document> documents;
    public String getSubjectInn() {
        return subjectInn;
    }

    public void setSubjectInn(String subjectInn) {
        this.subjectInn = subjectInn;
    }

    public String getSubjectSnils() {
        return subjectSnils;
    }

    public void setSubjectSnils(String subjectSnils) {
        this.subjectSnils = subjectSnils;
    }

    public String getSubjectFio() {
        return subjectFio;
    }

    public void setSubjectFio(String subjectFio) {
        this.subjectFio = subjectFio;
    }

    public LocalDate getSubjectBirthday() {
        return subjectBirthday;
    }

    public void setSubjectBirthday(LocalDate subjectBirthday) {
        this.subjectBirthday = subjectBirthday;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
