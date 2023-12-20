package org.edec.factSheet.model;

public class TypeFactSheetModel {

    private int idTypeFactSheet;
    private String title;

    public int getIdTypeFactSheet () {
        return idTypeFactSheet;
    }

    public void setIdTypeFactSheet (int idTypeFactSheet) {
        this.idTypeFactSheet = idTypeFactSheet;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    @Override
    public String toString () {
        return title;
    }
}
