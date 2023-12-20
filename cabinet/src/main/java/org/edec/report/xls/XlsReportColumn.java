package org.edec.report.xls;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class XlsReportColumn {
    public static final int TEXT_CENTER = 1;
    public static final int TYPE_STRING = 2;
    public static final int TYPE_NUMBER = 3;

    private String name;
    private String style;
    private String fieldName;
    private Integer width;
    private Boolean mergeNeighborCellsByValue;

    private int textAlign;
    private int cellType;

    public XlsReportColumn(String name, String fieldName, Integer width, boolean mergeNeighborCellsByValue) {
        this.width = width;
        this.name = name;
        this.fieldName = fieldName;
        this.mergeNeighborCellsByValue = mergeNeighborCellsByValue;
    }

    public void setCellType(int type) {
        this.cellType = type;
    }

    public void setTextAlign(int align) {
        this.textAlign = align;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, style, fieldName, mergeNeighborCellsByValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XlsReportColumn column = (XlsReportColumn) o;
        return Objects.equals(name, column.name) && Objects.equals(style, column.style) && Objects.equals(fieldName, column.fieldName) &&
               Objects.equals(mergeNeighborCellsByValue, column.mergeNeighborCellsByValue);
    }
}
