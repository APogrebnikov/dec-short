/*
 * 
 */
package org.edec.synchroMine.model.eso.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

/**
 * Класс Учебной группы.
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "dic_group")
public class DicGroup {
    @Id
    @Column(name = "id_dic_group")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Флаг, признак того, что запись активна (не удалена).(по умолчанию - 1)
     */
    @Column(name = "is_active")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean active;
    /**
     * Флаг, студенты УВЦ.(по умолчанию - 0)
     */
    @Column(name = "is_military")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean military;

    /**
     * Дата (год) формирования группы.
     */
    @Column
    private Date dateOfBegin;
    /**
     * Дата (год) расформирования группы.
     */
    @Column
    private Date dateOfEnd;

    /**
     * Ссылка на институт, к которому относится группа.
     */
    @Column(name = "id_institute")
    private Long instituteId;

    /**
     * Наименование учебной группы.
     */
    @Column
    private String groupname;

    /**
     * Ссылка на учебный план для группы.
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "id_curriculum")
    private Curriculum curriculum;

    /**
     * Ссылка на родительскую группу (при разбиении на подгруппы в процессе обучения).
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent_group")
    private DicGroup parentGroup;

    /**
     * Ссылка на список связей между группой и семестром.
     */
    @OneToMany(mappedBy = "dicGroup")
    private Set<LinkGroupSemester> listOfLinkGroupSemesters = new HashSet<>();
}
