package org.edec.commons.entity.mine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

/**
 * Все ведомости в DBO
 * Created by dmmax
 */
@Entity
@Table(name = "[Все_Ведомости]")
public class AllRegister
{
    @Id
    @Column(name = "Код")
    private int id;
    @Column(name = "Код_Группы")
    private int idGroup;

    @Column(name = "Дата_Экзамена")
    private Date dateExam;
    @Column(name = "ДатаСдачи")
    private Date dateSign;

    @Column(name = "Закрыта")
    private boolean isClose;
    @Column(name = "Дата_Закрытия")
    private Date dateClose;
    
    @Column(name = "ВсегоСтудентов")
    private int countStudent;
    @Column(name = "Оценок0")
    private int mark0;
    @Column(name = "Оценок1")
    private int mark1;
    @Column(name = "Оценок2")
    private int mark2;
    @Column(name = "Оценок3")
    private int mark3;
    @Column(name = "Оценок4")
    private int mark4;
    @Column(name = "Оценок5")
    private int mark5;
    @Column(name = "Семестр")
    private int semester;
    @Column(name = "Тип_Ведомости")
    private int typeVedom;
    @Column(name = "Дисциплина")
    private String subjectname;
    @Column(name = "Преподаватель")
    private String tutor;

    @OneToMany(mappedBy = "register",fetch = FetchType.LAZY)
    private Set<Marks> marksSet;

    public String getTutor() {
		return tutor;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public void setClose(boolean isClose) {
		this.isClose = isClose;
	}

	public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getIdGroup()
    {
        return idGroup;
    }

    public void setIdGroup(int idGroup)
    {
        this.idGroup = idGroup;
    }

    public Date getDateExam()
    {
        return dateExam;
    }

    public void setDateExam(Date dateExam)
    {
        this.dateExam = dateExam;
    }

    public int getCountStudent()
    {
        return countStudent;
    }

    public void setCountStudent(int countStudent)
    {
        this.countStudent = countStudent;
    }

    public int getMark0()
    {
        return mark0;
    }

    public void setMark0(int mark0)
    {
        this.mark0 = mark0;
    }

    public int getMark1()
    {
        return mark1;
    }

    public void setMark1(int mark1)
    {
        this.mark1 = mark1;
    }

    public int getMark2()
    {
        return mark2;
    }

    public void setMark2(int mark2)
    {
        this.mark2 = mark2;
    }

    public int getMark3()
    {
        return mark3;
    }

    public void setMark3(int mark3)
    {
        this.mark3 = mark3;
    }

    public int getMark4()
    {
        return mark4;
    }

    public void setMark4(int mark4)
    {
        this.mark4 = mark4;
    }

    public int getMark5()
    {
        return mark5;
    }

    public void setMark5(int mark5)
    {
        this.mark5 = mark5;
    }

    public int getSemester()
    {
        return semester;
    }

    public void setSemester(int semester)
    {
        this.semester = semester;
    }

    public int getTypeVedom()
    {
        return typeVedom;
    }

    public void setTypeVedom(int typeVedom)
    {
        this.typeVedom = typeVedom;
    }

    public String getSubjectname()
    {
        return subjectname;
    }

    public void setSubjectname(String subjectname)
    {
        this.subjectname = subjectname;
    }

    public Date getDateSign()
    {
        return dateSign;
    }

    public void setDateSign(Date dateSign)
    {
        this.dateSign = dateSign;
    }

	public boolean getIsClose() {
		return isClose;
	}

	public void setIsClose(boolean isClose) {
		this.isClose = isClose;
	}

	public Date getDateClose() {
		return dateClose;
	}

	public void setDateClose(Date dateClose) {
		this.dateClose = dateClose;
	}

    public boolean isClose() {
        return isClose;
    }

    public Set<Marks> getMarksSet() {
        return marksSet;
    }

    public void setMarksSet(Set<Marks> marksSet) {
        this.marksSet = marksSet;
    }

    @Override
    public String toString() {
        return "AllRegister{" +
                "id=" + id +
                ", idGroup=" + idGroup +
                ", dateExam=" + dateExam +
                ", dateSign=" + dateSign +
                ", isClose=" + isClose +
                ", dateClose=" + dateClose +
                ", countStudent=" + countStudent +
                ", mark0=" + mark0 +
                ", mark1=" + mark1 +
                ", mark2=" + mark2 +
                ", mark3=" + mark3 +
                ", mark4=" + mark4 +
                ", mark5=" + mark5 +
                ", semester=" + semester +
                ", typeVedom=" + typeVedom +
                ", subjectname='" + subjectname + '\'' +
                ", tutor='" + tutor + '\'' +
                '}';
    }
}
