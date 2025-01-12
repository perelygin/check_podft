package ptv.education.check_podft.model;


import jakarta.persistence.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name="subjects",  schema ="podft")
public class Subject {


    @Id
    @Column(name = "subject_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subjectId;
    @Column(name ="subject_type")
    private int subjectType;

    @Column(name ="subject_number_in_list")
    private int subjectNumberInList;
    @Column(name ="subject_inn")
    private String subjectInn;
    @Column(name ="subject_snils")
    private String subjectSnils;
    @Column(name ="subject_category")
    private String subjectCategory;
    @Column(name = "subject_fio")
    @NotEmpty(message="FIO cann't be empty")
    private String subjectFio;
    @Column(name ="subject_birthday")
    private LocalDate subjectBirthday;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents; //перечень документов

    public Subject() {
    }

    public Subject(int subjectType, int subjectNumberInList, String subjectInn, String subjectSnils, String subjectCategory, String subjectFio, LocalDate subjectBirthday) {
        this.subjectType = subjectType;
        this.subjectNumberInList = subjectNumberInList;
        this.subjectInn = subjectInn;
        this.subjectSnils = subjectSnils;
        this.subjectCategory = subjectCategory;
        this.subjectFio = subjectFio;
        this.subjectBirthday = subjectBirthday;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int id) {
        this.subjectId = id;
    }

    public int getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(int subject_type) {
        this.subjectType = subject_type;
    }

    public int getSubjectNumberInList() {
        return subjectNumberInList;
    }

    public void setSubjectNumberInList(int subject_number_in_list) {
        this.subjectNumberInList = subject_number_in_list;
    }

    public String getSubjectInn() {
        return subjectInn;
    }

    public void setSubjectInn(String subject_inn) {
        this.subjectInn = subject_inn;
    }

    public String getSubjectSnils() {
        return subjectSnils;
    }

    public void setSubjectSnils(String subject_snils) {
        this.subjectSnils = subject_snils;
    }

    public String getSubjectCategory() {
        return subjectCategory;
    }

    public void setSubjectCategory(String subject_category) {
        this.subjectCategory = subject_category;
    }

    public String getSubjectFio() {
        return subjectFio;
    }

    public void setSubjectFio(String subject_fio) {
        this.subjectFio = subject_fio;
    }

    public LocalDate getSubjectBirthday() {
        return subjectBirthday;
    }

    public void setSubjectBirthday(LocalDate subject_birthday) {
        this.subjectBirthday = subject_birthday;
    }

    public Optional<List<Document>> getDocuments() {
        return Optional.ofNullable(documents);
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + subjectId +
                ", subject_type=" + subjectType +
                ", subject_number_in_list=" + subjectNumberInList +
                ", subject_inn='" + subjectInn + '\'' +
                ", subject_snils='" + subjectSnils + '\'' +
                ", subject_category='" + subjectCategory + '\'' +
                ", subject_fio='" + subjectFio + '\'' +
                ", subject_birthday=" + subjectBirthday +
                '}';
    }
}
