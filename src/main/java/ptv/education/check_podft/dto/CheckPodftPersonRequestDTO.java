package ptv.education.check_podft.dto;

import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDate;

/*
   DTO  для POST-запроса на выполнение проверок по ИНН, по СНИЛС,  по ФИО + дата рождения
 */
public class CheckPodftPersonRequestDTO {
    @NotEmpty(message="INN cann't be empty")
    private String subjectInn;
    @NotEmpty(message="СНИЛС cann't be empty")
    private String subjectSnils;
    @NotEmpty(message="FIO cann't be empty")
    private String subjectFio;
   // @NotEmpty(message="Birthday cann't be empty")
    private LocalDate subjectBirthday;
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

}
