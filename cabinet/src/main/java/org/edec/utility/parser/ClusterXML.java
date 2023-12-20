package org.edec.utility.parser;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.edec.synchroMine.model.eso.entity.Parent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

public class ClusterXML {

    public void getClusterInfo(String mainPath, String clusterPath, String outPath, int num, int val, int next) {
        try {
            /**
             * Основной файл
             */
            FileInputStream main = new FileInputStream(mainPath);
            HSSFWorkbook wbM = new HSSFWorkbook(main);
            HSSFSheet dsM = wbM.getSheet("Результаты");
            Iterator<Row> iteratorM = dsM.iterator();

            /**
             * Файл для кластера
             */
            FileInputStream cluster = new FileInputStream(clusterPath);
            HSSFWorkbook wbC = new HSSFWorkbook(cluster);
            HSSFSheet dsC = wbC.getSheet("Cluster");
            Iterator<Row> iteratorC = dsC.iterator();

            while (iteratorC.hasNext()) {

                Row currentRowC = iteratorC.next();
                Cell numberCell = currentRowC.getCell(num);
                if (numberCell == null) {
                    break;
                }
                Integer count = 0;
                try {
                    count = Integer.valueOf((int) numberCell.getNumericCellValue());
                }catch (Exception e) {
                    continue;
                }

                Cell clusterCell = currentRowC.getCell(val);
                if (clusterCell == null) {
                    break;
                }
                Double clnum = clusterCell.getNumericCellValue();

                Row mainRow = dsM.getRow(count);
                if (mainRow != null) {
                    Cell clusterCellMain = mainRow.createCell(next);
                    clusterCellMain.setCellValue(clnum);
                }

            }
            wbM.write(new FileOutputStream(outPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
