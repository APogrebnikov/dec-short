package org.edec.rest;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lotus.domino.Document;
import lotus.domino.NotesException;
import org.apache.poi.ss.usermodel.Cell;
import org.edec.newOrder.service.orderCreator.other.MaterialSupportOrderService;
import org.edec.rest.ctrl.LotusCtrl;
import org.edec.synchroMine.model.eso.entity.Parent;
import org.edec.utility.parser.service.impl.ParentServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.zkoss.zul.South;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class LotusRunner {

    public static BigDecimal round(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal round2(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
    }

    static DecimalFormatSymbols symbols1 = new DecimalFormatSymbols();
    static DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    static DecimalFormat nf1 = new DecimalFormat("###0.00;###0.00", symbols1);
    static DecimalFormat nf = new DecimalFormat("#,##0.00;#,##0.00", symbols);

    public static void main(String[] args) {
        String fileName = "Эконом СВК-15 123124112312-холодная вода-23-AUG";
        String serialPart = fileName.split(" вода")[0];
        String service = serialPart.substring(serialPart.lastIndexOf('-'));
        System.out.println(service);
        serialPart = serialPart.replace(service, "");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(Integer.parseInt("11"));
        /*
        if (serialPart.contains("-")) {
            serialPart = serialPart.split("-")[0];
        }
        */
        System.out.println(passwordEncoder.encode("FHS7DFGF54HFhdf"));
        /*
        BigDecimal first = new BigDecimal("37574.61000000");
        BigDecimal second = new BigDecimal("944.51000000");
        BigDecimal print = first.add(second);
        System.out.println("1." + print);
        System.out.println("2." + round(print));
        System.out.println("3." + round2(print));

        BigDecimal prec = new BigDecimal(50);
        BigDecimal baseSum = first.multiply(prec).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        System.out.println("4." + baseSum);

        nf.format(first);
        */
        /*
        ParentServiceImpl parentService = new ParentServiceImpl();

        String fioParent = "Лимонтова Татьяна Викторовна";
        String group = "КИ21-02/1Б";
        String fioStudent = "Лимонтов Иван Владимирович";
        String email = "limontovat@mail.ru";
        String telnum = "89029750396";

        if (fioParent != "" && group != "" && fioStudent != "") {
            String family = fioStudent.split(" ")[0];
            String name = fioStudent.split(" ")[1];
            String patronimyc = fioStudent.split(" ")[2];
            Long scId = parentService.getOneStudentCardId(family, name, patronimyc, group);
            if (scId != null) {
                String familyP = fioParent.split(" ")[0];
                String nameP = fioParent.split(" ")[1];
                String patronimycP = fioParent.split(" ")[2];
                try {
                    Parent parent = new Parent();
                    parent.setFamily(familyP);
                    parent.setName(nameP);
                    parent.setPatronymic(patronimycP);
                    parent.setUsername("PA_" + cyr2lat(nameP).substring(0, 1).toUpperCase() + cyr2lat(familyP).substring(0, 1).toUpperCase() + cyr2lat(familyP.substring(1)));
                    parent.setPassword(generatePassword(8, 10));
                    parent.setEmail(email);
                    parent.setIdStudentCard(scId);
                    System.out.print(parent.getUsername() + " // ");
                    System.out.print(parent.getEmail() + " // ");
                    System.out.print(parent.getPassword() + "\n");
                    System.out.print(scId + "\n");
                    parentService.createParent(parent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        */
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


    //public static void main(String[] args) throws NotesException, DocumentException, IOException {
    //String re = "Разработка web-приложений";
    //System.out.println(re.toLowerCase().contains("Ра".toLowerCase()));

    // LotusCtrl lctrl = new LotusCtrl();
/*
        Document doc = lctrl.getDocumentById("2A76A");
        String number;
        number = doc.getItemValueInteger("Number") + "/";
        number += doc.getItemValueString("LNum");
        System.out.println(number);*/
    //lctrl.showInfo("309FE");
    //doc.remove(true);
/*
        Document doc2 = lctrl.getDocumentById("B4A");
        String number2;
        number2 = doc2.getItemValueInteger("Number") + "/";
        number2 += doc2.getItemValueString("LNum");
        System.out.println(number2);
        */
    //lctrl.copyDocFromYtoY("3D95E","2018", "2019");
    //lctrl.copyDocFromYtoY("3D95A","2018", "2019");
        /*
        try {
            lctrl.testSearch("ИКИТ о назначении соц. пов. стипендии с февраля (КОТОВ С.А.)");
        } catch (NotesException e) {
            e.printStackTrace();
        }*/
    // }

}
