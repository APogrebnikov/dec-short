package org.edec.utility.report.model.order;

import java.util.Date;

/**
 * Created by dmmax
 */
public class IndividualOrderModel {
    private Date date1;
    private Date date2;

    private Integer course;
    private Integer is_government_financed;
    private Integer qualification;
    private Integer sex;

    private String directioncode;
    private String directiontitle;
    private String economyformofstudy;

    private String fio;
    private String foundation;
    private String formofstudy;
    private String groupname;
    private String recordbook;
    private String speciality;
    private String specialitycode;
    private String specialitytitle;
    private String additional;

    public String getSpeciality () {
        OrderUtil orderUtil = new OrderUtil();

        String result = orderUtil.getSpeciallity(qualification, directioncode, directiontitle, specialitycode, specialitytitle);
        return result;
    }

    public String getEconomyformofstudy () {
        String result = "";
        if (is_government_financed == 1) {
            return "за счет бюджетных ассигнований федерального бюджета";
        } else {
            return "на условиях договора об оказании платных образовательных услуг";
        }
    }

    public Date getDate1 () {
        return date1;
    }

    public void setDate1 (Date date1) {
        this.date1 = date1;
    }

    public Date getDate2 () {
        return date2;
    }

    public void setDate2 (Date date2) {
        this.date2 = date2;
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public Integer getSex () {
        return sex;
    }

    public void setSex (Integer sex) {
        this.sex = sex;
    }

    public Integer getIs_government_financed () {
        return is_government_financed;
    }

    public void setIs_government_financed (Integer is_government_financed) {
        this.is_government_financed = is_government_financed;
    }

    public Integer getQualification () {
        return qualification;
    }

    public void setQualification (Integer qualification) {
        this.qualification = qualification;
    }

    public String getDirectioncode () {
        return directioncode;
    }

    public void setDirectioncode (String directioncode) {
        this.directioncode = directioncode;
    }

    public String getDirectiontitle () {
        return directiontitle;
    }

    public void setDirectiontitle (String directiontitle) {
        this.directiontitle = directiontitle;
    }

    public void setEconomyformofstudy (String economyformofstudy) {
        this.economyformofstudy = economyformofstudy;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getFoundation () {
        return foundation;
    }

    public void setFoundation (String foundation) {
        this.foundation = foundation;
    }

    public String getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (String formofstudy) {
        this.formofstudy = formofstudy;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public void setSpeciality (String speciality) {
        this.speciality = speciality;
    }

    public String getSpecialitycode () {
        return specialitycode;
    }

    public void setSpecialitycode (String specialitycode) {
        this.specialitycode = specialitycode;
    }

    public String getSpecialitytitle () {
        return specialitytitle;
    }

    public void setSpecialitytitle (String specialitytitle) {
        this.specialitytitle = specialitytitle;
    }

    public String getAdditional () {
        return additional;
    }

    public void setAdditional (String additional) {
        this.additional = additional;
    }
}
