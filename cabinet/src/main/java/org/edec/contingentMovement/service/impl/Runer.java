package org.edec.contingentMovement.service.impl;

import org.edec.contingentMovement.model.ReportModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Runer {

    public static void main(String[] args) throws ParseException {
        ReportService reportService = new ReportService();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<ReportModel> reportModels = reportService.initReport("1", dateFormat.parse("2019-02-28"), dateFormat.parse("2019-04-01"));
        System.out.println(reportService.printReport(reportModels));
    }
}
