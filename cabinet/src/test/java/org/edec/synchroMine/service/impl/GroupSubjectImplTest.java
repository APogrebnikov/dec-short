package org.edec.synchroMine.service.impl;

import org.edec.synchroMine.manager.groupSubject.EntityManagerGroupSubjectDBO;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupSubjectImplTest {

    private static EntityManagerGroupSubjectDBO groupSubjectDBO;

    //
    private static Long idInst;
    private static Integer course;
    private static Integer semester;
    private static String groupname;

    @BeforeAll
    static void init() {
        groupSubjectDBO = new EntityManagerGroupSubjectDBO();
        idInst = 27L;
        course = 4;
        semester = 8;
        groupname = "КИ12-18Б";
    }

    @Test
    void curriculum_subjects_not_empty() {
        List<SubjectGroupMineModel> subjectFromCurriculum = groupSubjectDBO.getSubjectFromCurriculum(idInst, course, semester, groupname, Collections.emptySet());
//        fail("");
        assertFalse(subjectFromCurriculum.size() == 0);
        subjectFromCurriculum.forEach(subject -> System.out.println(subject.toString()));
    }

    @Test
    void register_subjects_not_empty() {
        List<SubjectGroupMineModel> subjectByGroupRegisters = groupSubjectDBO.getSubjectByGroupRegisters(idInst, course, semester, groupname);
  //      fail("");
        System.out.println("======");
        assertFalse(subjectByGroupRegisters.size() == 0);
        subjectByGroupRegisters.forEach(subject -> System.out.println(subject.getSubjectname() + ": " + subject.getIdRegisterMine()));
    }
}