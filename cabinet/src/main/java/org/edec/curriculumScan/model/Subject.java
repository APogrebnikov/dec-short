package org.edec.curriculumScan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.constants.FormOfControlConst;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Subject {
    private Long id;
    // Дис
    private String name;
    // ИдетификаторДисциплины
    private String code;
    // ГОС
    private Float allHours = 0f;
    // СР
    private Float srHours = 0f;
    // Компетенции
    private List<Competence> competenceList = new ArrayList<>();
    // Кафедра
    private Long chairCode;
    // Форма контроля Экз/Зач/КП/КР
    private List<FormOfControlConst> fcList = new ArrayList<>();
    // Лекций - 101
    private Float lecHours = 0f;
    // Лабораторных - 103
    private Float labHours = 0f;
    // Практических - 107
    private Float praHours = 0f;
    // СРС - 108
    private Float ksrHours = 0f;
    // Экзаменационные - 108
    private Float examHours = 0f;
    // Номер семестра (Ном)
    private Integer semesterNumber;
    // Цикл
    private String cicleCode;
    // ID блока дисциплин, если есть
    private Long blockCode;
    // ID Dic-Subject, если дисциплина была уже сохранена
    private Long idDicSubject;
    // ID Mine-Subject
    private Long idMineSubject;

    // Экстра поля для синхронизации и сверку УП
    private Boolean isExam;
    private Boolean isPass;
    private Boolean isPractic;
    private Boolean isCP;
    private Boolean isCW;
    private String season;



    public Float getHoursSum () {
        return this.labHours + this.lecHours + this.ksrHours + this.praHours + this.examHours;
    }

    public Float getHoursSumWithoutKsr () {
        return this.labHours + this.lecHours + this.ksrHours + this.praHours + this.examHours;
    }

    public String getCompetenceString () {
        String res = "";
        this.getCompetenceList().sort(Comparator.comparing(p -> p.getName()));
        for (Competence competence : this.getCompetenceList()) {
            res += " " + competence.getName();
        }
        return res;
    }
}
