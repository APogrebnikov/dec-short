package org.edec.synchroMine;

import org.apache.commons.codec.binary.Base64;
import org.edec.synchroMine.model.eso.StudentModel;
import org.edec.utility.zk.DialogUtil;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    public static void main(String[] args){
        String snils = "12312312312";
        char[] arr = snils.toCharArray();
        String snilsForm = "";
        if (arr.length == 11) {
            snilsForm = arr[0] + arr[1] + arr[2] + "-" + arr[3] + arr[4] + arr[5] + "-" + arr[6] + arr[7] + arr[8] + " " + arr[9] + arr[10];
        }
        System.out.println(snilsForm);

        /*
        String[] splitFio = "Даудов Даниил Вахмуродович".split(" ");
        if (splitFio.length < 3) {
            System.out.println("Указано неверное ФИО! Введите ФИО через пробелы.");
            return;
        }
        // TODO: ИСПРАВИТЬ - возможно отсутствие Отчества
        String shortFio = splitFio[0] + " " + splitFio[1].substring(0, 1) + ". " + splitFio[2].substring(0, 1) + ".";
        System.out.println(shortFio);
        */
        /*
        String str = "ST00012|Name=ООО \"ЕИРЦ ТО\"|PersonalAcc=40702810300000040101|BankName=Банк ГПБ";
        str = str.substring(str.indexOf("|Name=") + 6);
        str = str.substring(0, str.indexOf("|"));
        System.out.println(str);
        */
        //double timing = 650 * 0.001;
        //System.out.println(String.valueOf(timing)+" "+timing);
        //String value = "UserName=80|108|97|116|111|85|115|101|114; expires=Thu, 12-Jan-2023 06:56:55 GMT; path=/; HttpOnly";
        //String token = value.substring(0, value.indexOf(";"));
        //System.out.println(token);
        /*
        byte[] encodedBytes = Base64.encodeBase64("yakuninyy:12345".getBytes());
        System.out.println("Basic " + new String(encodedBytes));
*/
        /*
        List<StudentModel> listStudent = new ArrayList<>();


        System.out.println("Hello!");
        StudentModel sm = new StudentModel();
        sm.setFio("Петров");
        sm.setIdSSS(23L);

        for (int i = 0; i<10; i++){
            if( i==5 ) {
                System.out.println(i+".  "+sm.getIdSSS());
            }
            else
            {
                System.out.println(i+".  "+sm.getFio()+"<<<<<<<");
            }
        }
        */


    }
}
