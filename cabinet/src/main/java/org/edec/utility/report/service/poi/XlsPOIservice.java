package org.edec.utility.report.service.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class XlsPOIservice {
    public byte[] getReportXLS (String fileName) {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Name 1");

        Row row = sheet.createRow(0);
        return null;
    }
}
