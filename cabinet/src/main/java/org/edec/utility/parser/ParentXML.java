package org.edec.utility.parser;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.Parent;
import org.edec.utility.parser.service.impl.ParentServiceImpl;
import org.hibernate.Query;
import org.hibernate.type.LongType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

public class ParentXML {

    ParentServiceImpl parentService = new ParentServiceImpl();

    public void getParentInfo(String inPath, String outPath) {
        try {
            FileInputStream fin = new FileInputStream(inPath);
            HSSFWorkbook wb = new HSSFWorkbook(fin);
            HSSFSheet ds = wb.getSheet("Студенты");
            Iterator<Row> iterator = ds.iterator();
            while (iterator.hasNext()) {
                String fioStudent = "";
                String group = "";
                String fioParent = "";
                String telnum = "";
                String email = "";

                Row currentRow = iterator.next();
                Integer startPoint = 0;
                //Iterator<Cell> cellIterator = currentRow.iterator();
                Cell fioCell = currentRow.getCell(startPoint);
                if (fioCell == null) {
                    break;
                }
                //if (fioCell.getCellType() == Cell.CELL_TYPE_STRING) {
                fioStudent = fioCell.getStringCellValue();
                if (fioStudent != "") {
//                    System.out.print(fioStudent + "--");
                } else {
//                    System.out.print("miss--");
                }

                Cell groupCell = currentRow.getCell(startPoint + 1);
                if (groupCell == null) {
                    continue;
                }
                group = groupCell.getStringCellValue();
                if (group != "") {
//                    System.out.print(group + "--");
                } else {
//                    System.out.print("miss--");
                }

                Cell fioParentCell = currentRow.getCell(startPoint + 2);
                fioParent = fioParentCell.getStringCellValue();
                if (fioParent != "") {
//                    System.out.print(fioParent + "--");
                } else {
//                    System.out.print("miss--");
                }

                Cell telParentCell = currentRow.getCell(startPoint + 4);
                if (telParentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    telnum = String.valueOf(telParentCell.getNumericCellValue());
                } else if (fioCell.getCellType() == Cell.CELL_TYPE_STRING) {
                    telnum = telParentCell.getStringCellValue();
                }
                if (telnum != "") {
//                    System.out.print(telnum + "--");
                } else {
//                    System.out.print("miss--");
                }

                Cell emailParentCell = currentRow.getCell(startPoint + 3);
                email = emailParentCell.getStringCellValue();
                if (email != "") {
//                    System.out.print(email + "--\n");
                } else {
//                    System.out.print("miss--\n");
                }

                if (fioParent != "" && group != "" && fioStudent != "" && !fioStudent.equals("ФИО")) {
                    String family = fioStudent.split(" ")[0];
                    String name = fioStudent.split(" ")[1];
                    String patronimyc = fioStudent.split(" ")[2];
                    Long scId = parentService.getOneStudentCardId(family, name, patronimyc, group);
                    if (scId != null) {
                        String familyP = fioParent.split(" ")[0];
                        String nameP = fioParent.split(" ")[1];
                        String patronimycP = fioParent.split(" ")[2];
                        try {
                            Parent cre = createParent(familyP, nameP, patronimycP, scId, email, telnum);
                            if (cre != null) {
                                Cell usernameRow = currentRow.createCell(7);
                                usernameRow.setCellValue(cre.getUsername());
                                Cell passwordRow = currentRow.createCell(8);
                                passwordRow.setCellValue(cre.getPassword());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            wb.write(new FileOutputStream(outPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Parent createParent(String family, String name, String patronymic, Long scId, String email, String telnum) throws Exception {
        //Создание объекта родителя
        Parent parent = new Parent();
        parent.setFamily(family);
        parent.setName(name);
        parent.setPatronymic(patronymic);
        parent.setUsername("PA_" + cyr2lat(name).substring(0, 1).toUpperCase() + cyr2lat(family).substring(0, 1).toUpperCase() + cyr2lat(family.substring(1)));
        parent.setPassword(generatePassword(8, 10));
        parent.setEmail(email);
        parent.setIdStudentCard(scId);
        System.out.print(parent.getUsername() + " // ");
        System.out.print(parent.getEmail() + " // ");
        System.out.print(parent.getPassword() + "\n");
        parentService.createParent(parent);
        return parent;
    }

    public static String generatePassword(int from, int to) {
        String pass = "";
        Random r = new Random();
        int cntchars = from + r.nextInt(to - from + 1);

        for (int i = 0; i < cntchars; ++i) {
            char next = 0;
            int range = 10;

            switch (r.nextInt(3)) {
                case 0: {
                    next = '0';
                    range = 10;
                }
                break;
                case 1: {
                    next = 'a';
                    range = 26;
                }
                break;
                case 2: {
                    next = 'A';
                    range = 26;
                }
                break;
            }

            pass += (char) ((r.nextInt(range)) + next);
        }

        return pass;
    }

    public static String cyr2lat(char ch) {
        switch (ch) {
            case 'а':
            case 'А':
                return "a";
            case 'б':
            case 'Б':
                return "b";
            case 'в':
            case 'В':
                return "v";
            case 'г':
            case 'Г':
                return "g";
            case 'д':
            case 'Д':
                return "d";
            case 'е':
            case 'Е':
                return "e";
            case 'ё':
            case 'Ё':
                return "e";
            case 'ж':
            case 'Ж':
                return "zh";
            case 'з':
            case 'З':
                return "z";
            case 'и':
            case 'И':
                return "i";
            case 'й':
            case 'Й':
                return "y";
            case 'к':
            case 'К':
                return "k";
            case 'л':
            case 'Л':
                return "l";
            case 'м':
            case 'М':
                return "m";
            case 'н':
            case 'Н':
                return "n";
            case 'о':
            case 'О':
                return "o";
            case 'п':
            case 'П':
                return "p";
            case 'р':
            case 'Р':
                return "r";
            case 'с':
            case 'С':
                return "s";
            case 'т':
            case 'Т':
                return "t";
            case 'у':
            case 'У':
                return "u";
            case 'ф':
            case 'Ф':
                return "f";
            case 'х':
            case 'Х':
                return "kh";
            case 'ц':
            case 'Ц':
                return "c";
            case 'ч':
            case 'Ч':
                return "ch";
            case 'ш':
            case 'Ш':
                return "sh";
            case 'щ':
            case 'Щ':
                return "sh";
            case 'ъ':
            case 'Ъ':
                return "";
            case 'ы':
            case 'Ы':
                return "ih";
            case 'ь':
            case 'Ь':
                return "";
            case 'э':
            case 'Э':
                return "e";
            case 'ю':
            case 'Ю':
                return "yu";
            case 'я':
            case 'Я':
                return "ya";
            default:
                return String.valueOf(ch);
        }
    }

    public static String cyr2lat(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (char ch : s.toCharArray()) {
            sb.append(cyr2lat(ch));
        }
        return sb.toString();
    }
}
