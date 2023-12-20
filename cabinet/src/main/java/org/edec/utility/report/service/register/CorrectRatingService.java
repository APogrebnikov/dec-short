package org.edec.utility.report.service.register;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.report.manager.CorrectReportDAO;
import org.edec.utility.report.model.register.CorrectRatingJasperModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CorrectRatingService {
    private CorrectReportDAO correctReportDAO = new CorrectReportDAO();

    public JRBeanCollectionDataSource getBeanData(CorrectRequestModel correctRequest) {
        List<CorrectRatingJasperModel> data = new ArrayList<>();

        CorrectRatingJasperModel single = correctReportDAO.getCorrectReportModel(correctRequest);
        SimpleDateFormat dt1 = new SimpleDateFormat("dd.MM.yyyy");
        single.setOldRating(RatingConst.getNameByRating(
                correctRequest.getOldRating() == null ? single.getOldRatingVal() : correctRequest.getOldRating()));
        single.setRegisterDate(dt1.format(single.getRegisterDateVal()));
        single.setRegisterNumber("№" + single.getRegisterNumber());
        single.setSigndate(dt1.format(new Date()));
        single.setCertnumber(correctRequest.getCertnumber());
        single.setNewRating(RatingConst.getNameByRating(correctRequest.getNewRating()));
        single.setSignatorytutor(getShortFIO(single.getSignatorytutor()));

        data.add(single);
        return new JRBeanCollectionDataSource(data);
    }

    public String getShortFIO(String fio) {
        String[] fioArr = fio.split(" ");
        String result = fioArr[0];
        if (fioArr.length > 1) {
            result += " " + fioArr[1].substring(0, 1) + ".";
        }
        if (fioArr.length > 2) {
            result += " " + fioArr[2].substring(0, 1) + ".";
        }
        return result;
    }
}
