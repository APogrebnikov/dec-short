package org.edec.newOrder.service.orderCreator.concept;

import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.converter.DateConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SocialDateGetter {

    //Получаем дату c (для социальной и социальной повышенной стипендии)
    public static Date getSocialScholarshipDateFrom(OrderCreateStudentModel student){
        //Если студент не получал соц стипендию до этого момента
        //или предыдущая стипендия истекает раньше чем было подана справка
        //то дата с будет равняться дате начала действия справки
        if ((student.getDateSocialScholarshipTo() == null) ||
            (student.getDateSocialScholarshipTo().before(student.getDateReferenceFrom()))) {
            return student.getDateReferenceFrom();
        } else if (student.getDateSocialScholarshipTo().equals(student.getDateReferenceFrom()) ||
                   student.getDateSocialScholarshipTo().after(student.getDateReferenceFrom())) {
            //Если предыдущая стипендия истекает в тот же день что и начинается действие справки или позднее
            //то датой с указываем следующий день после окончания действия предыдущей стипендии
            return DateConverter.getNextDay(student.getDateSocialScholarshipTo());
        }

        return null;
    }

    //Получаем дату по (только для социальной)
    public static Date getSocialScholarshipDateTo(OrderCreateStudentModel student){
        //Если дата окончания действия справки позже чем дата окончания обучения или справка бессрочна
        //то датой по ставим дату окончания периода дипломирования
        //иначе ставим дату окончания действия справки
        if (student.getDateReferenceTo() == null && student.getDateOfEndEducation()!=null
            || student.getDateReferenceTo().after(student.getDateOfEndEducation())) {
            return student.getDateOfCertificationTo();
        } else {
            return student.getDateReferenceTo();
        }
    }

    //Получаем дату по (только для социальной повышенной)
    public static Date getSocialIncreasedScholarshipDateTo(OrderCreateStudentModel student){
        //Выставляем дату по
        //Если студент выпускник
        //то датой по ставим дату окончания обучения
        //если дата окончания справки позже чем дата окончания сессии
        //то датой по ставим округленное значение окончания сессии
        //иначе ставим дату окончания действия справки

        if((Math.round(student.getPeriodOfStudy() * 2)) == student.getSemesternumber()) {
            student.setDateSocialScholarshipTo(student.getDateOfEndEducation());
        } else if (student.getDateReferenceTo()==null || student.getDateReferenceTo().after(student.getDateOfEndSession())){

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(student.getDateOfEndSession());

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            return calendar.getTime();
        }
        else {
            return student.getDateReferenceTo();
        }

        return null;
    }
}
