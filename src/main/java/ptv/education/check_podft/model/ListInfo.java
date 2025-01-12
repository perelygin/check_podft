package ptv.education.check_podft.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/*
содержит информацию о текущем перечне
 */
@Entity
@Table(name="list_info",  schema ="podft")
public class ListInfo {

    @Id
    @Column(name = "list_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int listId;
    @Column(name ="Format_version")
    private String FormatVersion;  //версия формата
    @Column(name ="list_date")
    private LocalDate listDate;   // Дата переченя
    @Column(name ="list_prev_date")
    private LocalDate listPrevDate; //Дата предыдущего перечня

    public ListInfo() {
    }

    public ListInfo(String format_version, LocalDate list_date, LocalDate listPrevDate) {
        FormatVersion = format_version;
        this.listDate = list_date;
        this.listPrevDate = listPrevDate;
    }

    public ListInfo(String formatVersion, LocalDate listDate) {
        FormatVersion = formatVersion;
        this.listDate = listDate;
    }

    public String getFormatVersion() {
        return FormatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        FormatVersion = formatVersion;
    }

    public LocalDate getListDate() {
        return listDate;
    }

    public void setListDate(LocalDate listDate) {
        this.listDate = listDate;
    }

    public LocalDate getListPrevDate() {
        return listPrevDate;
    }

    public void setListPrevDate(LocalDate listPrevDate) {
        this.listPrevDate = listPrevDate;
    }
}
