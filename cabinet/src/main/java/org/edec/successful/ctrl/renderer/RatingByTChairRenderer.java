package org.edec.successful.ctrl.renderer;

import org.edec.successful.model.*;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RatingByTChairRenderer implements ListitemRenderer<DepartmentModel> {
    List<StudentModel> allFullStudents = new ArrayList<>();
    List<StudentModel> all5Students = new ArrayList<>();
    List<StudentModel> all45Students = new ArrayList<>();
    List<StudentModel> all4Students = new ArrayList<>();
    List<StudentModel> all34Students = new ArrayList<>();
    List<StudentModel> all3Students = new ArrayList<>();
    List<StudentModel> all23Students = new ArrayList<>();
    List<StudentModel> missStudents = new ArrayList<>();
    List<StudentModel> miss1Students = new ArrayList<>();
    List<StudentModel> miss2Students = new ArrayList<>();
    List<StudentModel> miss34Students = new ArrayList<>();
    List<StudentModel> miss5MStudents = new ArrayList<>();
    List<StudentModel> missFullStudents = new ArrayList<>();
    public Integer totalCountallFullStudents = 0;
    public Integer totalCountall5Students = 0;
    public Integer totalCountall45Students = 0;
    public Integer totalCountall4Students = 0;
    public Integer totalCountall34Students = 0;
    public Integer totalCountall3Students = 0;
    public Integer totalCountall23Students = 0;
    public Integer totalCountmissStudents = 0;
    public Integer totalCountmiss1Students = 0;
    public Integer totalCountmiss2Students = 0;
    public Integer totalCountmiss34Students = 0;
    public Integer totalCountmiss5MStudents = 0;
    public Integer totalCountmissFullStudents = 0;

    public Integer totalCountStudent = 0;

    public Listfoot currentFooter;

    List<DepartmentModel> departmentModel;

    public RatingByTChairRenderer (List<DepartmentModel> departmentModel) {
        this.departmentModel = departmentModel;
    }

    public RatingByTChairRenderer () {

    }

    @Override
    public void render (Listitem li, DepartmentModel data, int index) throws Exception {
        allFullStudents = new ArrayList<>();
        all5Students = new ArrayList<>();
        all45Students = new ArrayList<>();
        all4Students = new ArrayList<>();
        all34Students = new ArrayList<>();
        all3Students = new ArrayList<>();
        all23Students = new ArrayList<>();
        missStudents = new ArrayList<>();
        miss1Students = new ArrayList<>();
        miss2Students = new ArrayList<>();
        miss34Students = new ArrayList<>();
        miss5MStudents = new ArrayList<>();
        missFullStudents = new ArrayList<>();

        getAll(data);
        li.setValue(data);

        //#
        new Listcell(String.valueOf(index + 1)).setParent(li);
        //Курс
        Listcell listCellCourse = new Listcell(String.valueOf(data.getFulltitle()));
        listCellCourse.setTooltiptext(String.valueOf(data.getFulltitle()));
        listCellCourse.setParent(li);

        listCellCourse.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            List<GroupModel> groups = ((DepartmentModel) ((Listitem) (event.getTarget().getParent())).getValue()).getGroups();
            Listbox par = ((Listbox) li.getParent());
            RatingByGroupRenderer ratingByGroupRenderer = new RatingByGroupRenderer(null, departmentModel);
            par.setItemRenderer(ratingByGroupRenderer);
            par.setModel(new ListModelList<>(groups));
            par.renderAll();
            currentFooter.getChildren().clear();
            ratingByGroupRenderer.calcFooter(currentFooter);
        });

        //Кол-во людей
        new Listcell(String.valueOf(data.getCount())).setParent(li);
        //Сдали
        Integer percent = allFullStudents.size() * 100 / data.getCount();
        Listcell allFullLC = new Listcell(String.valueOf(allFullStudents.size()) + " (" + percent + "%)");
        allFullLC.setParent(li);
        final List<StudentModel> allFullStudentsLocal = allFullStudents;
        allFullLC.addEventListener(
                Events.ON_CLICK, (EventListener<MouseEvent>) event -> DialogUtil.info(allFullStudentsLocal.size() + "", "Сдали"));
        //5
        Listcell all5LC = new Listcell(String.valueOf(all5Students.size()));
        all5LC.setParent(li);
        final List<StudentModel> all5StudentsLocal = all5Students;
        all5LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(all5StudentsLocal);
        });
        //5 и 4
        Listcell all45LC = new Listcell(String.valueOf(all45Students.size()));
        all45LC.setParent(li);
        final List<StudentModel> all45StudentsLocal = all45Students;
        all45LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(all45StudentsLocal);
        });
        //4
        Listcell all4LC = new Listcell(String.valueOf(all4Students.size()));
        all4LC.setParent(li);
        final List<StudentModel> all4StudentsLocal = all4Students;
        all4LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(all4StudentsLocal);
        });
        //4 и 3
        Listcell all34LC = new Listcell(String.valueOf(all34Students.size()));
        all34LC.setParent(li);
        final List<StudentModel> all34StudentsLocal = all34Students;
        all34LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(all34StudentsLocal);
        });
        //3
        Listcell all3LC = new Listcell(String.valueOf(all3Students.size()));
        all3LC.setParent(li);
        final List<StudentModel> all3StudentsLocal = all3Students;
        all3LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(all3StudentsLocal);
        });
        //3 и 2
        Listcell all23LC = new Listcell(String.valueOf(all23Students.size()));
        all23LC.setParent(li);
        final List<StudentModel> all23StudentsLocal = all23Students;
        all23LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(all23StudentsLocal);
        });
        //Есть долги
        Integer percentMiss = missStudents.size() * 100 / data.getCount();
        Listcell missLC = new Listcell(String.valueOf(missStudents.size()) + " (" + percentMiss + "%)");
        missLC.setParent(li);
        final List<StudentModel> missStudentsLocal = missStudents;
        missLC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(missStudentsLocal);
        });
        //Имеет 1 долг
        Listcell miss1LC = new Listcell(String.valueOf(miss1Students.size()));
        miss1LC.setParent(li);
        final List<StudentModel> miss1StudentsLocal = miss1Students;
        miss1LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(miss1StudentsLocal);
        });
        //Имеет 2 долга
        Listcell miss2LC = new Listcell(String.valueOf(miss2Students.size()));
        miss2LC.setParent(li);
        final List<StudentModel> miss2StudentsLocal = miss2Students;
        miss2LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(miss2StudentsLocal);
        });
        //Имеет 3-4 долга
        Listcell miss34LC = new Listcell(String.valueOf(miss34Students.size()));
        miss34LC.setParent(li);
        final List<StudentModel> miss34StudentsLocal = miss34Students;
        miss34LC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(miss34StudentsLocal);
        });
        //Имеет 5 и более долгов
        Listcell miss5MLC = new Listcell(String.valueOf(miss5MStudents.size()));
        miss5MLC.setParent(li);
        final List<StudentModel> miss5MStudentsLocal = miss5MStudents;
        miss5MLC.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(miss5MStudentsLocal);
        });
        //Не сдана вся сессия
        Integer percentMissFull = missFullStudents.size() * 100 / data.getCount();
        Listcell missFull = new Listcell(String.valueOf(missFullStudents.size()) + " (" + percentMissFull + "%)");
        missFull.setParent(li);
        final List<StudentModel> missFullStudentsLocal = missFullStudents;
        missFull.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(missFullStudentsLocal);
        });

        totalCountStudent += data.getCount();
        totalCountallFullStudents += allFullStudents.size();
        totalCountall5Students += all5Students.size();
        totalCountall45Students += all45Students.size();
        totalCountall4Students += all4Students.size();
        totalCountall34Students += all34Students.size();
        totalCountall3Students += all3Students.size();
        totalCountall23Students += all23Students.size();
        totalCountmissStudents += missStudents.size();
        totalCountmiss1Students += miss1Students.size();
        totalCountmiss2Students += miss2Students.size();
        totalCountmiss34Students += miss34Students.size();
        totalCountmiss5MStudents += miss5MStudents.size();
        totalCountmissFullStudents += missFullStudents.size();
    }

    public void getAll (DepartmentModel data) {
        for (GroupModel groupModel : data.getGroups()) {
            for (StudentModel studentModel : groupModel.getStudents()) {
                //System.out.println(studentModel.getFio());
                int pass = 0;
                int five = 0;
                int four = 0;
                int three = 0;
                int two = 0;
                int miss = 0;
                int total = 0;
                for (SubjectModel subjectModel : studentModel.getSubjects()) {

                    Integer maxRating = subjectModel.getLastRating().getRating();

                    //Если дисциплина изучалась то считаем
                    if (maxRating != RatingConst.NOT_LEARNED.getRating()) {
                        total++;
                    }

                    //System.out.println(subjectModel.getSubjectName()+"  "+subjectModel.getLastRating().getRating());
                    if (maxRating == RatingConst.EXCELLENT.getRating()) {
                        five++;
                    }
                    if (maxRating == RatingConst.PASS.getRating()) {
                        pass++;
                    }
                    if (maxRating == RatingConst.GOOD.getRating()) {
                        four++;
                    }
                    if (maxRating == RatingConst.SATISFACTORILY.getRating()) {
                        three++;
                    }
                    if (maxRating == RatingConst.UNSATISFACTORILY.getRating() || maxRating == RatingConst.NOT_PASS.getRating()) {
                        two++;
                    }
                    if (maxRating == RatingConst.FAILED_TO_APPEAR.getRating() || maxRating == RatingConst.ZERO.getRating()) {
                        miss++;
                    }
                }
                //System.out.println(five+" "+four+" "+three+" "+two+" "+miss+" "+studentModel.getSubjects().size());
                if ((five + pass) == total) {
                    all5Students.add(studentModel);
                    allFullStudents.add(studentModel);
                    //System.out.println("5");
                    //System.out.println("+"+allFullStudents.size());
                }

                if ((four + pass) == total && four > 0) {
                    all4Students.add(studentModel);
                    allFullStudents.add(studentModel);
                    //System.out.println("4");
                    //System.out.println("+"+allFullStudents.size());
                }

                if ((three + pass) == total && three > 0) {
                    all3Students.add(studentModel);
                    allFullStudents.add(studentModel);
                    //System.out.println("3");
                    //System.out.println("+"+allFullStudents.size());
                }

                if (five > 0 && four > 0 && three == 0 && two == 0 && miss == 0) {
                    all45Students.add(studentModel);
                    allFullStudents.add(studentModel);
                    //System.out.println("45");
                    //System.out.println("+"+allFullStudents.size());
                }

                if ((five > 0 || four > 0) && three > 0 && two == 0 && miss == 0) {
                    all34Students.add(studentModel);
                    allFullStudents.add(studentModel);
                    //System.out.println("34");
                    //System.out.println("+"+allFullStudents.size());
                }

                if (three > 0 && two > 0 && miss == 0) {
                    all23Students.add(studentModel);
                    //System.out.println("23");
                }

                if ((miss + two == 1) && (miss + two) != total) {
                    miss1Students.add(studentModel);
                    missStudents.add(studentModel);
                    //System.out.println("1 miss");
                    //System.out.println("-"+missStudents.size());
                }

                if (((miss + two) == 2) && (miss + two) != total) {
                    miss2Students.add(studentModel);
                    missStudents.add(studentModel);
                    //System.out.println("2 miss");
                    //System.out.println("-"+missStudents.size());
                }

                if (((miss + two) == 3 || (miss + two) == 4) && (miss + two) != total) {
                    miss34Students.add(studentModel);
                    missStudents.add(studentModel);
                    //System.out.println("3-4 miss");
                    //System.out.println("-"+missStudents.size());
                }

                if ((miss + two) >= 5 && (miss + two) != total) {
                    miss5MStudents.add(studentModel);
                    missStudents.add(studentModel);
                    //System.out.println("5> miss");
                    //System.out.println("-"+missStudents.size());
                }

                if ((miss + two) == total) {
                    missFullStudents.add(studentModel);
                    missStudents.add(studentModel);
                    //System.out.println("Full miss");
                    //System.out.println("-"+missStudents.size());
                }

                //System.out.println("-------");
            }
        }
    }

    public void calcFooter (Listfoot lfStudent) {
        currentFooter = lfStudent;
        currentFooter.getChildren().clear();
        Listfooter lfStudent1 = new Listfooter("");
        lfStudent1.setParent(lfStudent);

        Listfooter lfStudentCourse = new Listfooter("Сумма:");
        lfStudentCourse.setParent(lfStudent);

        Listfooter lfStudentAll = new Listfooter(totalCountStudent.toString());
        lfStudentAll.setParent(lfStudent);
        lfStudentAll.setTooltiptext("Кол-во студентов");
        lfStudentAll.setStyle("border: 1px solid #bfbfbf;");
        Integer percent = 0;
        if (totalCountStudent != 0) {
            percent = totalCountallFullStudents * 100 / totalCountStudent;
        }
        Listfooter lfallFullStudents = new Listfooter(totalCountallFullStudents.toString() + " (" + percent + "%)");
        lfallFullStudents.setParent(lfStudent);
        lfallFullStudents.setTooltiptext("Сдали");
        lfallFullStudents.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfall5Students = new Listfooter(totalCountall5Students.toString());
        lfall5Students.setParent(lfStudent);
        lfall5Students.setTooltiptext("Все 5");
        lfall5Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfall45Students = new Listfooter(totalCountall45Students.toString());
        lfall45Students.setParent(lfStudent);
        lfall45Students.setTooltiptext("На 4 и 5");
        lfall45Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfall4Students = new Listfooter(totalCountall4Students.toString());
        lfall4Students.setParent(lfStudent);
        lfall4Students.setTooltiptext("Все 4");
        lfall4Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfall34Students = new Listfooter(totalCountall34Students.toString());
        lfall34Students.setParent(lfStudent);
        lfall34Students.setTooltiptext("На 3 и 4-5");
        lfall34Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfall3Students = new Listfooter(totalCountall3Students.toString());
        lfall3Students.setParent(lfStudent);
        lfall3Students.setTooltiptext("Все 3");
        lfall3Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfall23Students = new Listfooter(totalCountall23Students.toString());
        lfall23Students.setParent(lfStudent);
        lfall23Students.setTooltiptext("На 2 и 3");
        lfall23Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfmissStudents = new Listfooter(totalCountmissStudents.toString());
        lfmissStudents.setParent(lfStudent);
        lfmissStudents.setTooltiptext("Долги");
        lfmissStudents.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfmiss1Students = new Listfooter(totalCountmiss1Students.toString());
        lfmiss1Students.setParent(lfStudent);
        lfmiss1Students.setTooltiptext("1 долг");
        lfmiss1Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfmiss2Students = new Listfooter(totalCountmiss2Students.toString());
        lfmiss2Students.setParent(lfStudent);
        lfmiss2Students.setTooltiptext("2 долга");
        lfmiss2Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfmiss34Students = new Listfooter(totalCountmiss34Students.toString());
        lfmiss34Students.setParent(lfStudent);
        lfmiss34Students.setTooltiptext("3-4 долга");
        lfmiss34Students.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfmiss5MStudents = new Listfooter(totalCountmiss5MStudents.toString());
        lfmiss5MStudents.setParent(lfStudent);
        lfmiss5MStudents.setTooltiptext("Более 5");
        lfmiss5MStudents.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfmissFullStudents = new Listfooter(totalCountmissFullStudents.toString());
        lfmissFullStudents.setParent(lfStudent);
        lfmissFullStudents.setTooltiptext("Не сдал полностью");
        lfmissFullStudents.setStyle("border: 1px solid #bfbfbf;");
    }

    public void showStudents (List<StudentModel> students) {
        DialogUtil.info(concat(students), "Студнеты");
    }

    public String concat (List<StudentModel> students) {
        StringBuilder res = new StringBuilder();
        Collections.sort(students, Comparator.comparing(StudentModel::getFio));
        for (int i = 0; i < students.size(); i++) {
            res.append(i + 1).append(". ").append(students.get(i).getFio()).append("\n");
        }
        return res.toString();
    }

    public List<DepartmentModel> getDepartmentModel () {
        return departmentModel;
    }

    public void setDepartmentModel (List<DepartmentModel> departmentModel) {
        this.departmentModel = departmentModel;
    }
}
