package ptv.education.check_podft.util;

/*
 ответ на запрос по проверке ФЛ
 */
public class CheckPodftPersonResponse {
    private boolean FoundPersonsInn;  //результат проверки по ИНН. true - найден
    private boolean FoundPersonsSnils; //результат проверки по СНИЛС. true - найден
    private boolean FoundPersonsFioBirthday; //результат проверки по ФИО и дате рождения true - найден

    public CheckPodftPersonResponse(boolean FoundPersonsInn, boolean FoundPersonsSnils, boolean FoundPersonsFioBirthday) {
        this.FoundPersonsInn = FoundPersonsInn;
        this.FoundPersonsSnils = FoundPersonsSnils;
        this.FoundPersonsFioBirthday = FoundPersonsFioBirthday;
    }

    public boolean isFoundPersonsInn() {
        return FoundPersonsInn;
    }

    public void setFoundPersonsInn(boolean foundPersonsInn) {
        this.FoundPersonsInn = foundPersonsInn;
    }

    public boolean isFoundPersonsSnils() {
        return FoundPersonsSnils;
    }

    public void setFoundPersonsSnils(boolean foundPersonsSnils) {
        this.FoundPersonsSnils = foundPersonsSnils;
    }

    public boolean isFoundPersonsFioBirthday() {
        return FoundPersonsFioBirthday;
    }

    public void setFoundPersonsFioBirthday(boolean foundPersonsFioBirthday) {
        this.FoundPersonsFioBirthday = foundPersonsFioBirthday;
    }
}
