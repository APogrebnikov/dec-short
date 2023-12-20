/*
 * 
 */
package org.edec.synchroMine.model.eso.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Класс Связь между группой и семестром.
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "link_group_semester")
public class LinkGroupSemester {

    @Id
    @Column(name = "id_link_group_semester")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата начала семестра.
     */
    @Column
    private Date dateOfBeginSemester;
    /**
     * Дата окончания семестра.
     */
    @Column
    private Date dateOfEndSemester;
    /**
     * Дата начала сессии.
     */
    @Column
    private Date dateOfBeginSession;
    /**
     * Дата окончания сессии.
     */
    @Column
    private Date dateOfEndSession;
    /**
     * Дата начала зачётной недели.
     */
    @Column
    private Date dateOfBeginPassWeek;
    /**
     * Дата окончания зачётной недели.
     */
    @Column
    private Date dateOfEndPassWeek;
    /**
     * Дата начала каникул.
     */
    @Column
    private Date dateOfBeginVacation;
    /**
     * Дата окончания каникул.
     */
    @Column
    private Date dateOfEndVacation;
    /**
     * Дата начала выплаты стипендии.
     */
    @Column
    private Date dateOfBeginGrant;
    /**
     * Дата окончания выплаты стипендии.
     */
    @Column
    private Date dateOfEndGrant;

    /**
     * Номер семестра для группы(по умолчанию - 0).
     */
    @Column
    private int semesterNumber;
    /**
     * Курс.
     */
    @Column
    private int course;

    /**
     * Ссылка на семестр.
     */
    @Column(name = "id_semester")
    private Long semesterId;

    @Column
    private Long otherdbid;

    /**
     * Ссылка на группу.
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "id_dic_group")
    private DicGroup dicGroup;
}
