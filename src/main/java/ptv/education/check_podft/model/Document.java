package ptv.education.check_podft.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="documents",  schema ="podft")
public class Document {

    @Id
    @Column(name = "doc_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int doc_id;
//    @Column(name = "owner_id")
//    private int owner_fio_id;
    @Column(name = "doc_type")
    private String doc_type;
    @Column(name = "doc_ser")
    private String doc_ser;
    @Column(name = "doc_number")
    private String doc_number;
    @Column(name = "doc_organ")
    private String doc_organ ;
    @Column(name = "doc_date_begin")
    private LocalDate doc_date_begin;
    @ManyToOne  //для дочерней сущности
    @JoinColumn(name = "owner_id",referencedColumnName = "subject_id")   //внешний ключ.  name - имя поля в дочерней таблице.  referencedColumnName - имя поля в родительской таблице
    private Subject person; //террорист -  владелец документов.  Родительская сущность
    public Document() {
    }

    public Document(String doc_type, String doc_ser, String doc_number, String doc_organ, LocalDate doc_date_begin, Subject person) {
       // this.owner_fio_id = owner_fio_id;
        this.doc_type = doc_type;
        this.doc_ser = doc_ser;
        this.doc_number = doc_number;
        this.doc_organ = doc_organ;
        this.doc_date_begin = doc_date_begin;
        this.person = person;
    }

    public int getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(int id) {
        this.doc_id = id;
    }

//    public int getOwner_fio_id() {
//        return owner_fio_id;
//    }
//
//    public void setOwner_fio_id(int subject_id) {
//        this.owner_fio_id = subject_id;
//    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public String getDoc_ser() {
        return doc_ser;
    }

    public void setDoc_ser(String doc_ser) {
        this.doc_ser = doc_ser;
    }

    public String getDoc_number() {
        return doc_number;
    }

    public void setDoc_number(String doc_number) {
        this.doc_number = doc_number;
    }

    public String getDoc_organ() {
        return doc_organ;
    }

    public void setDoc_organ(String doc_organ) {
        this.doc_organ = doc_organ;
    }

    public LocalDate getDoc_date_begin() {
        return doc_date_begin;
    }

    public void setDoc_date_begin(LocalDate doc_date_begin) {
        this.doc_date_begin = doc_date_begin;
    }

    public Subject getPerson() {
        return person;
    }

    public void setPerson(Subject person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "Document{" +
                "doc_id=" + doc_id +
                ", doc_type='" + doc_type + '\'' +
                ", doc_ser='" + doc_ser + '\'' +
                ", doc_number='" + doc_number + '\'' +
                ", doc_organ='" + doc_organ + '\'' +
                ", doc_date_begin=" + doc_date_begin +
                ", person=" + person +
                '}';
    }
}
