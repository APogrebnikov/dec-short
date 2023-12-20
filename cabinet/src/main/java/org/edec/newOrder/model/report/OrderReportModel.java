package org.edec.newOrder.model.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.report.model.order.OrderUtil;
import org.json.JSONObject;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderReportModel {
    private String description, descriptionOneDates, descriptionTwoDates, foundation, fio, groupname, recordbook, additional, sessionresult, prevOrderNum, prevOrderDate, descriptionDate, subDescription, academicalPerformance, nomination;
    private String directioncode, directiontitle, economyformofstudy, formofstudy, speciality, specialitycode, specialitytitle, scholarship, INN, schoolYear;
    private Integer course, is_government_financed, qualification, typeScholarship, sex;
    private String practicType, practicName, pageHeader;
    private Long idStudentMine, idOrderSection;
    private Date beginYear, endYear, firstDateStudent, date1, date2, datePracticFrom, datePracticTo;
    private Boolean governmentFinanced, prolongation, sirota, invalid, indigent, veteran, isForeigner;

    public void setAdditional(String additional) {
        this.additional = additional;
        if (additional != null && !additional.equals("")) {
            JSONObject additionalJSON = new JSONObject(additional);

            if (additionalJSON.has(OrderStudentJSONConst.SESSION_RESULT)) {
                this.sessionresult = additionalJSON.getString(OrderStudentJSONConst.SESSION_RESULT);
            }
            if (additionalJSON.has(OrderStudentJSONConst.SCHOLARSHIP)) {
                this.scholarship = additionalJSON.getString(OrderStudentJSONConst.SCHOLARSHIP);
            }
            if (additionalJSON.has(OrderStudentJSONConst.TYPE_SCHOLARSHIP)) {
                this.typeScholarship = additionalJSON.getInt(OrderStudentJSONConst.TYPE_SCHOLARSHIP);
            }

            if (additionalJSON.has(OrderStudentJSONConst.ACADEMIC_PERFORMANCE)){
                this.academicalPerformance = additionalJSON.getString(OrderStudentJSONConst.ACADEMIC_PERFORMANCE);
            }

            if(additionalJSON.has(OrderStudentJSONConst.NOMINATION)){
                this.nomination = additionalJSON.getString(OrderStudentJSONConst.NOMINATION);
            }
        }
    }

    public String getSpeciality () {
        OrderUtil orderUtil = new OrderUtil();

        String result = orderUtil.getSpeciallity(qualification, directioncode, directiontitle, specialitycode, specialitytitle);
        return result;
    }

    public String getEconomyformofstudy () {
        if (is_government_financed == 1) {
            return "за счет бюджетных ассигнований федерального бюджета";
        } else {
            return "на условиях договора об оказании платных образовательных услуг";
        }
    }
}
