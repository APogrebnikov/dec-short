package org.edec.parentsImport.ctrl;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.edec.synchroMine.model.eso.entity.Parent;
import org.edec.utility.parser.ParentXML;
import org.edec.utility.parser.service.impl.ParentServiceImpl;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;

import java.io.*;
import java.util.Iterator;


public class IndexPageCtrl extends CabinetSelector  {

    @Wire
    private Button btnUploadFile;

    private ParentXML parentXML = new ParentXML();
    ParentServiceImpl parentService = new ParentServiceImpl();
    private Media media;

    @Override
    protected void fill() throws InterruptedException {

    }

    @Listen("onUpload = #btnUploadFile")
    public void uploadFile(UploadEvent event){
        media = event.getMedia();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(media.getStreamData());
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet ds = wb.getSheet("Студенты");
            Iterator<Row> iterator = ds.iterator();
            while (iterator.hasNext()) {
                String fioStudent = "";
                String group = "";
                String fioParent = "";
                String telnum = "";
                String email = "";

                Row currentRow = iterator.next();
                Integer startPoint = 1;
                Cell fioCell = currentRow.getCell(startPoint);
                if (fioCell == null) {
                    break;
                }

                fioStudent = fioCell.getStringCellValue();
                if (fioStudent != "") {
                    System.out.print(fioStudent + "--");
                } else {
                    System.out.print("miss--");
                    PopupUtil.showError("Удалите пустые столбцы в начале документа!");
                    return;
                }

                Cell groupCell = currentRow.getCell(startPoint+1);
                if (groupCell == null){ continue; }
                group = groupCell.getStringCellValue();
                if (group != "") {
                    System.out.print(group + "--");
                } else {
                    System.out.print("miss--");
                }

                Cell fioParentCell = currentRow.getCell(startPoint+2);
                fioParent = fioParentCell.getStringCellValue();
                if (fioParent != "") {
                    System.out.print(fioParent + "--");
                } else {
                    System.out.print("miss--");
                }

                Cell telParentCell = currentRow.getCell(startPoint+4);
                if (telParentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    telnum = String.valueOf(telParentCell.getNumericCellValue());
                } else if (fioCell.getCellType() == Cell.CELL_TYPE_STRING) {
                    telnum = telParentCell.getStringCellValue();
                }
                if (telnum != "") {
                    System.out.print(telnum + "--");
                } else {
                    System.out.print("miss--");
                }

                Cell emailParentCell = currentRow.getCell(startPoint+3);
                email = emailParentCell.getStringCellValue();
                if (email != "") {
                    System.out.print(email + "--\n");
                } else {
                    System.out.print("miss--\n");
                }

                if (fioParent != "" && group != "" && fioStudent != "") {
                    String family = fioStudent.split(" ")[0];
                    String name = fioStudent.split(" ")[1];
                    String patronimyc = "";
                    if (fioStudent.split(" ").length > 2) {
                        patronimyc = fioStudent.split(" ")[2];
                    }
                    Long scId = parentService.getOneStudentCardId(family, name, patronimyc, group);
                    if (scId != null) {
                        String familyP = fioParent.split(" ")[0];
                        String nameP = fioParent.split(" ")[1];
                        String patronimycP = "";
                        if (fioParent.split(" ").length > 2) {
                            patronimycP = fioParent.split(" ")[2];
                        }
                        try {
                            Parent cre= parentXML.createParent(familyP, nameP, patronimycP, scId, email, telnum);
                            if(cre!=null){
                                Cell usernameRow=currentRow.createCell(5);
                                usernameRow.setCellValue(cre.getUsername());
                                Cell passwordRow=currentRow.createCell(6);
                                passwordRow.setCellValue(cre.getPassword());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            File file = new File("parent_users.xls");
            wb.write(new FileOutputStream(file));
            Filedownload.save(file, null);
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

}
