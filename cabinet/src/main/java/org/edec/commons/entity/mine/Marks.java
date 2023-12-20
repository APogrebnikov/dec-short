package org.edec.commons.entity.mine;

import org.edec.synchroMine.model.mine.Student;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "[Оценки]")
public class Marks
{
    /**
     * Идентификатор оценок
     */
    @Id
    @Column(name = "Код")
    private Integer id;

    /**
     * Идентификатор ведомости
     */
    @Column(name = "Код_Ведомости")
    private Integer idRegister;

    /**
     * Дата основной сдачи
     */
    @Column(name = "Дата_Сдачи")
    private Date dateResultMark;
    
    /**
     * Оценка основной сдачи
     */
    @Column(name = "[Оценка_На_Экзамене(Зачете)]")
    private Integer mainMark;

	/**
     * Дата пересдачи 1
     */
    @Column(name = "Дата_Пересдачи1")
    private Date dateRetake1;

	/**
     * Дата пересдачи2
     */
    @Column(name = "Дата_Пересдачи2")
    private Date dateRetake2;

    /**
     * Дата пересдачи3
     */
    @Column(name = "Дата_Пересдачи3")
    private Date dateRetake3;

    /**
     * Номер в ведомости
     */
    @Column(name = "Номер_В_Ведомости")
    private Integer numberAtRegister;

    /**
     * Самая-самая итоговая оценка
     */
    @Column(name = "Итог")
    private Integer result;

    /**
     * Оценка основной сдачи
     */
    @Column(name = "Итоговая_Оценка")
    private Integer resultMark;

    /**
     * Оценка пересадчи1
     */
    @Column(name = "Пересдача1")
    private Integer retake1;

    /**
     * Оценка пересдачи2
     */
    @Column(name = "Пересдача2")
    private Integer retake2;

    /**
     * Оценка пересдачи3
     */
    @Column(name = "Пересдача3")
    private Integer retake3;

    /**
     * Тема курсовой
     */
    @Column(name = "ТемаКурсовой")
    private String themeOfCP;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "\"Код_Студента\"", insertable=false, updatable=false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"Код_Ведомости\"", insertable=false, updatable=false)
    private AllRegister register;

    public int getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public int getIdRegister()
    {
        return idRegister;
    }

    public void setIdRegister(Integer idRegister)
    {
        this.idRegister = idRegister;
    }

    public Date getDateResultMark()
    {
        return dateResultMark;
    }

    public void setDateResultMark(Date dateResultMark)
    {
        this.dateResultMark = dateResultMark;
    }

    public Date getDateRetake1()
    {
        return dateRetake1;
    }

    public void setDateRetake1(Date dateRetake1)
    {
        this.dateRetake1 = dateRetake1;
    }

    public Date getDateRetake2()
    {
        return dateRetake2;
    }

    public void setDateRetake2(Date dateRetake2)
    {
        this.dateRetake2 = dateRetake2;
    }

    public Date getDateRetake3()
    {
        return dateRetake3;
    }

    public void setDateRetake3(Date dateRetake3)
    {
        this.dateRetake3 = dateRetake3;
    }

    public int getNumberAtRegister()
    {
        return numberAtRegister;
    }

    public void setNumberAtRegister(Integer numberAtRegister)
    {
        this.numberAtRegister = numberAtRegister;
    }

    public Integer getResult()
    {
        return result;
    }

    public void setResult(Integer result)
    {
        this.result = result;
    }

    public Integer getResultMark()
    {
        return resultMark;
    }

    public void setResultMark(Integer resultMark)
    {
    	this.result=resultMark;
        this.resultMark = resultMark;
    }

    public Integer getRetake1()
    {
        return retake1;
    }

    public void setRetake1(Integer retake1)
    {
    	this.result=retake1;
        this.retake1 = retake1;
    }

    public Integer getRetake2()
    {
        return retake2;
    }

    public void setRetake2(int retake2)
    {
    	this.result=retake2;
        this.retake2 = retake2;
    }

    public int getRetake3()
    {
        return retake3;
    }

    public void setRetake3(Integer retake3)
    {
    	//this.result=retake3;
        this.retake3 = retake3;
    }

    public String getThemeOfCP()
    {
        return themeOfCP;
    }

    public void setThemeOfCP(String themeOfCP)
    {
        this.themeOfCP = themeOfCP;
    }
    
    
    public Integer getMainMark() {
		return mainMark;
	}

	public void setMainMark(Integer mainMark) {
		this.mainMark = mainMark;
	}

    @Override
    public String toString() {
        return "Marks{" +
                "id=" + id +
                ", idRegister=" + idRegister +
                ", dateResultMark=" + dateResultMark +
                ", mainMark=" + mainMark +
                ", dateRetake1=" + dateRetake1 +
                ", dateRetake2=" + dateRetake2 +
                ", dateRetake3=" + dateRetake3 +
                ", numberAtRegister=" + numberAtRegister +
                ", result=" + result +
                ", resultMark=" + resultMark +
                ", retake1=" + retake1 +
                ", retake2=" + retake2 +
                ", retake3=" + retake3 +
                ", themeOfCP='" + themeOfCP + '\'' +
                '}';
    }

    public void print(){
        System.out.println(this);
    }

    public void setRetake2(Integer retake2) {
        this.retake2 = retake2;
    }

    public AllRegister getRegister() {
        return register;
    }

    public void setRegister(AllRegister register) {
        this.register = register;
    }
}
