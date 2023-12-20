package org.edec.report.xls;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class XlsReport<DATATYPE> {
    private List<XlsReportColumn> columns;
    private HSSFWorkbook report;

    private List<DATATYPE> data = new ArrayList<>();

    public XlsReport(List<DATATYPE> data) {
        columns = new ArrayList<>();
        setData(data);
    }

    public void setData(List<DATATYPE> data) {
        if(data == null) {
            throw new IllegalArgumentException("Переданный список является null");
        }

        this.data = data;
    }

    public XlsReport addColumn(String name, String fieldName, Integer width) {
        return addColumn(name, fieldName, width, false);
    }

    public XlsReport addColumn(String name, String fieldName, Integer width, boolean mergeCellsWithSameValue, int... cellStyle) {
        if(columns.stream().noneMatch(el -> el.getName().equals(fieldName))) {
            columns.add(new XlsReportColumn(name, fieldName, width, mergeCellsWithSameValue));
            return this;
        } else {
            throw new IllegalArgumentException("Колонка с именем " + fieldName + " уже есть в списке");
        }
    }

    public XlsReport addColumnAt(String name, String fieldName, Integer width, int index) {
        return addColumnAt(name, fieldName, width, index, false);
    }

    public XlsReport addColumnAt(String name, String fieldName, Integer width, int index, boolean mergeCellsWithSameValue) {
        if(index < 0 || index >= data.size()) {
            throw new IllegalArgumentException("Индекс должен попадать в диапазон от 0 до " + data.size() + ", получено значение " + index);
        }

        if(columns.stream().noneMatch(el -> el.getName().equals(name))) {
            columns.add(index, new XlsReportColumn(name, fieldName, width, mergeCellsWithSameValue));
            return this;
        } else {
            throw new IllegalArgumentException("Колонка с именем " + name + " уже есть в списке");
        }
    }

    public void setColumnType(String columnName, int columnType) {
        XlsReportColumn col = columns.stream().filter(el -> el.getName().equals(columnName)).findAny().orElse(null);

        if(col == null) {
            throw new IllegalArgumentException("Колонки " + columnName + " не существует в отчете");
        }

        col.setCellType(columnType);
    }

    public boolean removeColumn(String columnName) {
        if(columnName == null || columnName.equals("")) {
            return false;
        }

        int index = columns.indexOf(columns.stream().filter(el -> el.getName().equals(columnName)).findFirst().orElse(null));

        if(index == -1) {
            return false;
        } else {
            return removeColumn(index);
        }
    }

    public boolean removeColumn(int index) {
        if(index < 0 || index >= columns.size()) {
            return false;
        }

        columns.remove(index);
        return true;
    }

    public void setColumnsOrder(List<String> columnsList) {
        if (columnsList == null) {
            throw new IllegalArgumentException("Список не должен быть null");
        }

        if (columnsList.size() != columns.size()) {
            throw new IllegalArgumentException("Количество переданных колонок должно совпадать с количеством колонок в отчете");
        }

        for(XlsReportColumn column : columns) {
            String columnName = columnsList.stream().filter(el -> el.equals(column.getName())).findFirst().orElse(null);

            if(columnName == null) {
                throw new IllegalArgumentException("Колонки " + column.getName() + " не существует в отчете");
            }
        }

        List<XlsReportColumn> columnsWithNewOrder = new ArrayList<>();

        for(String columnName : columnsList) {
            // Добавляем колонку в конец нового списка если найдена
            columns.stream().filter(el -> el.getName().equals(columnName)).findFirst().ifPresent(columnsWithNewOrder::add);
        }

        assert columnsList.size() == columnsWithNewOrder.size();
    }

    public List<String> getListColumnsName() {
        return columns.stream().map(XlsReportColumn::getName).collect(Collectors.toList());
    }

    public HSSFWorkbook generateReport() throws IllegalStateException {
        if(data == null) {
          throw new IllegalStateException("Не заполнены данные для отчета");
        }

        if(columns == null || columns.size() == 0) {
            throw new IllegalStateException("Отсутствуют колонки для печати");
        }

        report = new HSSFWorkbook();
        HSSFSheet sheet = report.createSheet("Лист 1");

        Row row;
        Cell cell;
        int rowNum = 0;
        int cellNum = 0;

        //configure header
        row = sheet.createRow(rowNum);
        for(XlsReportColumn column : columns) {
            if(column.getFieldName() == null || column.getFieldName().equals("")) {
                throw new IllegalStateException("Не заполнено datasource поле для колонки");
            }

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(column.getName());
            cell.setCellStyle(getStyleForHeader());
            cellNum++;
        }

        rowNum++;
        cellNum = 0;

        HSSFCellStyle cellStyle = report.createCellStyle();
        Font font = report.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short)12);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        //fill report
        for(int i = 0; i < data.size(); i++) {
            DATATYPE data = this.data.get(i);
            row = sheet.createRow(rowNum);

            for(XlsReportColumn column : columns) {
                String fieldValue = getFieldValueFromModel(data, column.getFieldName());

                cell = row.createCell(cellNum, getCellTypeForColumn(column.getCellType()));

                // TODO REFACTOR!
                if(column.getCellType() == XlsReportColumn.TYPE_NUMBER) {
                    cell.setCellValue(Integer.parseInt(fieldValue));
                } else {
                    cell.setCellValue(fieldValue);
                }
                cell.setCellStyle(cellStyle);

                // проверяем можем ли мы мерджить ячейки
                if(column.getMergeNeighborCellsByValue() && i != 0) {
                    DATATYPE prevData = this.data.get(i - 1);
                    String prevFieldValue = getFieldValueFromModel(prevData, column.getFieldName());

                    // если текущая ячейка не совпадет по значению, то попытаемся найти область ячеек с одинаковым значением
                    if(!prevFieldValue.equals(fieldValue)) {
                        int j = i - 1;
                        boolean found = false;
                        for(; j >= 0; j--) {
                            DATATYPE prevDataToMerge = this.data.get(j);
                            String prevFieldToMergeValue = getFieldValueFromModel(prevDataToMerge, column.getFieldName());

                            // если появилась ячейка с новым значением
                            if(!prevFieldToMergeValue.equals(prevFieldValue)) {
                                found = true;
                                break;
                            }
                        }

                        if(j == 0 && i > 1 && !found) {
                            sheet.addMergedRegion(new CellRangeAddress(j + 1, i, cellNum, cellNum));
                        } else if(j == 0 && i > 1 && found && j + 2 < i) {
                            sheet.addMergedRegion(new CellRangeAddress(j + 2, i, cellNum, cellNum));
                        } else if(j + 2 < i) {
                            sheet.addMergedRegion(new CellRangeAddress(j + 2, i, cellNum, cellNum));
                        }
                    }
                }

                cellNum++;
            }

            rowNum++;
            cellNum = 0;
        }

        try {
            report.close();

            for(int i = 0; i < columns.size(); i++) {
                sheet.setColumnWidth(i, PoiPixelUtil.pixel2WidthUnits(columns.get(i).getWidth()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("При формировании отчета возникла ошибка");
        }

        return report;
    }

    private CellType getCellTypeForColumn(int type) {
        switch (type) {
            case XlsReportColumn.TYPE_NUMBER:
                return CellType.NUMERIC;
        }

        return CellType.STRING;
    }

    private HSSFCellStyle getStyleForHeader() {
        HSSFFont font = report.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short)14);

        HSSFCellStyle style = report.createCellStyle();
        style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    private String getFieldValueFromModel(DATATYPE data, String fieldName) throws IllegalStateException {
        try {
            Field field = data.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType().getName().equals("java.lang.String")) {
                return (String)field.get(data);
            } else {
                Class<?> targetType = field.getType();
                Object objectValue = targetType.newInstance();

                return convertFieldValueToString(field.get(objectValue));
            }
        } catch (NoSuchFieldException fieldEx) {
            throw new IllegalStateException("Не найдено поле " + fieldName + " в модели");
        } catch (IllegalAccessException accesEx) {
            throw new IllegalStateException("Не удалось получить доступ к полю " + fieldName + " в модели");
        } catch (InstantiationException instEx) {
            throw new IllegalStateException("Не удалось инстанциировать поле " + fieldName + " в модели");
        }
    }

    private String convertFieldValueToString(Object value) {
        if(value == null) {
            return "";
        }

        if(value instanceof Date) {
            return new SimpleDateFormat("dd.MM.yyyy").format((Date)value);
        }

        return value.toString();
    }


}
