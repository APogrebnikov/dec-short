package org.edec.rest.ctrl;
public class LotusCtrl{

}
/*
package org.edec.rest.ctrl;

import lotus.domino.*;
import org.apache.log4j.Logger;
import org.edec.rest.model.MessageLotus;
import org.json.JSONObject;

import javax.ws.rs.*;
import java.io.File;
import java.util.*;


@Path("/lotus")
public class LotusCtrl {
    private static final Logger log = Logger.getLogger(LotusCtrl.class.getName());

    private static final String DOMAIN = "domino21.sfu-kras.ru:63148";
    private static final String USER = "APogrebnikov";
    private static final String PASSWORD = "$sfukras$";
    //Тестовая база
     //private static final String DB_NAME = "OfficeMediaSFU\\DELO\\ITORD439_students_test.nsf";
    //Основная база на 2017 год
    private static final String DB_NAME = "OfficeMediaSFU\\DELO\\ITORD439_students_" + Calendar.getInstance().get(Calendar.YEAR) + ".nsf";
    private Session session = null;

    private Database connection () {
        try {
            session = NotesFactory.createSession(DOMAIN, USER, PASSWORD);
            return session.getDatabase(null, DB_NAME);
        } catch (NotesException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Database connectionToYear (String year) {
        try {
            String localDBName= "OfficeMediaSFU\\DELO\\ITORD439_students_" + year + ".nsf";
            session = NotesFactory.createSession(DOMAIN, USER, PASSWORD);
            return session.getDatabase(null, localDBName);
        } catch (NotesException e) {
            e.printStackTrace();
            return null;
        }
    }

    @POST
    @Path("/create")
    @Consumes("application/json;charset=utf-8")
    public String createOrder (MessageLotus mes) {
        log.info("Создание в лотусе приказа(" + mes.getPath() + ")");
        Database db = connection();
        JSONObject jsonObject = new JSONObject();
        String id_doc = "";
        String regular = "((\\\\)|(/))(order)*(\\d)*(\\u002E)(pdf)";
        mes.setPath(mes.getPath().toLowerCase());
        if (mes.getPath().endsWith(".pdf")) {
            mes.setPath(mes.getPath().replaceAll(regular, ""));
        }
        try {
            Document doc = db.createDocument();
            doc.appendItemValue("Form", "ORD");//Использовать ORD для приказов
            doc.appendItemValue("DocSpecType", "по личному составу студентов");//Спец тип - только такой и будет
            doc.appendItemValue("DocType", "ПРИКАЗ");//Статик
            doc.appendItemValue("DocType2", "ПРИКАЗ");//Статик
            doc.appendItemValue("OrderType", "по личному составу студентов");//Нужно уточнить, но пока оставить
            doc.appendItemValue("Prior", "Обычно");//статик
            doc.appendItemValue("Priority", "Обычно");//статик
            doc.appendItemValue(
                    "ShortCName",
                    "Федеральное государственное образовательное учреждение высшего образования «Сибирский федеральный университет» (СФУ)"
            );//статик
            doc.appendItemValue("Sign", "");//оставить пустым при создании
            doc.appendItemValue("Status", "Проект");//статик
            doc.appendItemValue("Subject", mes.getSubject());//Название приказа, который нужен пережать
            //doc.appendItemValue("AprName", "");//Кто утвердил (возможно не нужен будет)
            //doc.appendItemValue("AprPost", "");//Должность (возможно не нужен будет)
            //doc.appendItemValue("FirmFName", "");//Название фирмы или хз что это (возможно не нужно будет)
            RichTextItem body = doc.createRichTextItem("Body");
            body.appendText("Приложение: ");
            File dirFile = new File(mes.getPath());
            for (File file : dirFile.listFiles()) {
                if (file.isFile()) {
                    body.addNewLine(2);
                    body.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file.getPath(), "order");
                }
            }
            doc.replaceItemValue("Body", body);
            if (doc.save(true, true)) {
                id_doc = doc.getNoteID();
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
        jsonObject.put("iddoc", id_doc == null ? "" : id_doc);
        log.info("Приказ в лотусе создан и его ID:" + jsonObject.getString("iddoc"));
        return jsonObject.toString();
    }

    @GET
    @Produces("application/json;charset=utf-8")
    public String getNumberByIdDoc (@QueryParam("id_doc") String id_doc) throws NotesException {
        Database db = connection();
        JSONObject jsonObject = new JSONObject();
        String number = "";
        log.info("Получение номера приказа с ID:" + id_doc);
        try {
            Document document = db.getDocumentByID(id_doc);
            if (document.getNoteID().toString().equals(id_doc)) {
                number = document.getItemValueInteger("Number") + "/";
                number += document.getItemValueString("LNum");
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
        jsonObject.put("number", number.equals("0/null") || number.equals("0/") || number.equals("null/null") ? "" : number);
        log.info("Номер получен и он: " + jsonObject.getString("number"));
        return jsonObject.toString();
    }

    @PUT
    @Path("/update")
    @Consumes("application/json;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public String updateDocument (MessageLotus mes) {
        if (mes.getIddoc() == null || mes.getIddoc().equals("")) {
            return new JSONObject().put("status", "SUCCESS").toString();
        }
        log.info("Обновление документов приказа(" + mes.getPath() + ") в лотусе с идентификатором: " + mes.getIddoc());
        Database db = connection();
        JSONObject jsonObject = new JSONObject();
        String regular = "((\\\\)|(/))(order)*(\\d)*(\\u002E)(pdf)";
        mes.setPath(mes.getPath().toLowerCase());
        if (mes.getPath().endsWith(".pdf")) {
            mes.setPath(mes.getPath().replaceAll(regular, ""));
        }
        String status;
        try {
            Document document = db.getDocumentByID(mes.getIddoc());

            Vector v = document.getItems();
            Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                Item item = (Item) e.nextElement();
                if (item.getName().equals("$FILE")) {
                    item.remove();
                }
            }

            RichTextItem oldBody = (RichTextItem) document.getFirstItem("Body");
            oldBody.remove();

            RichTextItem newBody = document.createRichTextItem("Body");

            RichTextParagraphStyle rts = bodyStyle(session);
            newBody.appendParagraphStyle(rts);

            newBody.appendText("Приказ: ");
            File dirFile = new File(mes.getPath());
            for (File file : dirFile.listFiles()) {
                if (file.isFile()) {
                    newBody.addNewLine(1);
                    newBody.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file.getPath(), "order");
                }
            }

            newBody.addNewLine(1);
            newBody.appendText("Приложения: ");
            newBody.addNewLine(1);
            int x = 0;
            for (File file : dirFile.listFiles()) {
                if (file.isDirectory() && file.getName().equals("attach")) {
                    File dirAttach = new File(file.getPath());
                    for (File fileAttach : dirAttach.listFiles()) {
                        x++;
                        newBody.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, fileAttach.getPath(), "attach_" + x);
                        newBody.addNewLine(1);
                    }
                }
            }
            document.save(true, true);
            status = "SUCCESS";
        } catch (NotesException e) {
            e.printStackTrace();
            status = "ERROR";
        }
        jsonObject.put("status", status);
        if (status.equals("ERROR")) {
            log.info("Обновление документов приказа(" + mes.getPath() + ") в лотусе прошло неудачно");
        } else {
            log.info("Обновление документов приказа(" + mes.getPath() + ") в лотусе прошло успешно");
        }
        return jsonObject.toString();
    }

    @PUT
    @Path("/updateAttach")
    @Consumes("application/json;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public String updateAttach (MessageLotus mes) {
        Database db = connection();
        JSONObject jsonObject = new JSONObject();
        String status;
        String regular = "((\\\\)|(/))(order)*(\\d)*(\\u002E)(pdf)";
        mes.setPath(mes.getPath().toLowerCase());
        if (mes.getPath().endsWith(".pdf")) {
            mes.setPath(mes.getPath().replaceAll(regular, ""));
        }
        try {
            Document document = db.getDocumentByID(mes.getIddoc());

            Vector v = document.getItems();
            Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                Item item = (Item) e.nextElement();
                if (item.getName().equals("$FILE")) {
                    item.remove();
                }
            }

            document.replaceItemValue("Subject", mes.getSubject()); //Название приказа, который нужен пережать на всякий случай

            RichTextItem oldBody = (RichTextItem) document.getFirstItem("Body");
            oldBody.remove();

            RichTextItem newBody = document.createRichTextItem("Body");

            RichTextParagraphStyle rts = bodyStyle(session);
            newBody.appendParagraphStyle(rts);

            newBody.appendText("Приказ: ");
            File dirFile = new File(mes.getPath());
            for (File file : dirFile.listFiles()) {
                if (file.isFile()) {
                    newBody.addNewLine(1);
                    newBody.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file.getPath(), "order");
                }
            }

            newBody.addNewLine(1);
            newBody.appendText("Приложения: ");
            newBody.addNewLine(1);
            int x = 0;
            for (File file : dirFile.listFiles()) {
                if (file.isDirectory() && file.getName().equals("attach")) {
                    File dirAttach = new File(file.getPath());
                    for (File fileAttach : dirAttach.listFiles()) {
                        ++x;
                        newBody.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, fileAttach.getPath(), "attach_" + x);
                        newBody.addNewLine(1);
                    }
                }
            }
            document.save(true, true);
            status = "SUCCESS";
        } catch (NotesException e) {
            e.printStackTrace();
            status = "ERROR";
        }
        jsonObject.put("status", status);
        return jsonObject.toString();
    }

    public String updateNumber (String idDoc, Integer number, String postfixNumber) {
        Database db = connection();
        try {
            assert db != null;
            Document document = db.getDocumentByID(idDoc);
            System.out.println(document.getItemValue("Subject"));
            System.out.println(document.getItemValue("Number"));
            System.out.println(document.getItemValueString("LNum"));
            //if (document.getItemValueString("Number")!=null||document.getItemValueString("LNum")!=null)
            //    return null;
            document.appendItemValue("Number", number);
            document.appendItemValue("LNum", postfixNumber);
            if (document.save()) {
                return number + "/" + postfixNumber;
            }
            return null;
        } catch (NotesException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void showInfo(String idDoc) {
        Database db = connection();
        try {
            assert db != null;
            Document document = db.getDocumentByID(idDoc);
            Vector v = document.getItems();

            Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                Item item = (Item) e.nextElement();
                System.out.println(item.getName() + ": " + item.getText());
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
    }

    private RichTextParagraphStyle bodyStyle (Session s) {
        RichTextParagraphStyle style = null;
        try {
            style = s.createRichTextParagraphStyle();
            style.setAlignment(RichTextParagraphStyle.ALIGN_LEFT);
            style.setSpacingAbove(RichTextParagraphStyle.SPACING_SINGLE);
            style.setSpacingBelow(RichTextParagraphStyle.SPACING_SINGLE);
            style.setInterLineSpacing(RichTextParagraphStyle.SPACING_SINGLE);
            style.setLeftMargin(RichTextParagraphStyle.RULER_ONE_INCH);
            style.setFirstLineLeftMargin((int) (RichTextParagraphStyle.RULER_ONE_INCH * 1.5));
            style.setLeftMargin(RichTextParagraphStyle.TAB_LEFT);
            style.setRightMargin(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return style;
    }

    public Document getDocumentById (String idDoc) {
        Database db = connection();
        Document document = null;
        try {
            document = db.getDocumentByID(idDoc);
        } catch (NotesException e) {
            e.printStackTrace();
        }
        return document;
    }

    public Document copyDocFromYtoY(String idDoc, String fromYear, String toYear) {
        Database dbFrom = connectionToYear(fromYear);
        Database dbTo = connectionToYear(toYear);

        Document document = null;
        try {
            document = dbFrom.getDocumentByID(idDoc);
            Document newDoc = document.copyToDatabase(dbTo);
            System.out.println("Note Id: " + newDoc.getNoteID());
            return newDoc;
        } catch (NotesException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void testSearch(String nameForSearch) throws NotesException {
        Database dbTo = connectionToYear("2019");
        DocumentCollection list = dbTo.getAllDocuments();
        Document tmpdoc;
        Document doc = list.getFirstDocument();
        while (doc != null) {
            if (doc.getItemValueString("Subject")!=null) {
                if (doc.getItemValueString("Subject").equals(nameForSearch)) {
                    System.out.println(doc.getNoteID() + " " + doc.getItemValueString("Subject"));
                    System.out.println("-" + doc.getItemValueString("Status"));
                }
            }
            tmpdoc = list.getNextDocument();
            doc.recycle();
            doc = tmpdoc;
        }
    }
}
*/
