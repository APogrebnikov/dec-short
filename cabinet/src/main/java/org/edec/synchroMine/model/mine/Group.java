package org.edec.synchroMine.model.mine;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
@Entity
@Table(name = "[Все_Группы]")
public class Group {
    @Id
    @Column(name = "Код")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Курс")
    private Integer course;

    @Column(name = "Код_Факультета")
    private Long idInst;

    @Column(name = "Название")
    private String name;

    @Transient
    private List<Student> students = new ArrayList<>();

    public Group () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Long getIdInst () {
        return idInst;
    }

    public void setIdInst (Long idInst) {
        this.idInst = idInst;
    }

    public List<Student> getStudents () {
        return students;
    }

    public void setStudents (List<Student> students) {
        this.students = students;
    }
}
