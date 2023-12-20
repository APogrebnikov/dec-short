package org.edec.commons.entity.dec.mine;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "link_register", schema = "mine")
public class LinkRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer course;
    @Column(name = "semester_number", nullable = false)
    private Integer semesterNumber;
    @Column(name = "course_mine")
    private Integer courseMine;
    @Column(name = "semester_number_mine")
    private Integer semesterNumberMine;

    @Column(nullable = false)
    private String groupname;
    @Column(name = "subjectname_cabinet", nullable = false)
    private String subjectnameCabinet;
    @Column(name = "subjectname_mine")
    private String subjectnameMine;
}
