package org.edec.synchroMine;


import org.edec.curriculumScan.model.Subject;
import org.edec.synchroMine.manager.subjectSynch.EntityManagerSubjectDBO;
import org.edec.synchroMine.manager.subjectSynch.EntityManagerSubjectESO;
import org.edec.synchroMine.model.CurriculumCompareScanResult;
import org.edec.synchroMine.service.CurriculumComparatorService;
import org.edec.synchroMine.service.impl.CurriculumComparatorImpl;

import java.util.List;

public class RunableTest {

    public static void main(String[] args){

        EntityManagerSubjectESO esoManager = new EntityManagerSubjectESO();
        EntityManagerSubjectDBO dboManager = new EntityManagerSubjectDBO();

        CurriculumComparatorService ccs = new CurriculumComparatorImpl();

        List<Subject> esoList = esoManager.getSubjectsByLGS("КИ17-01",null);
        List<Subject> dboList = dboManager.getSubjectsByLGS("КИ17-01",null);

        List<CurriculumCompareScanResult> result = ccs.compareSubjectsFromDB(esoList, dboList, "КИ17-02");
        ccs.generateCompareReport(result);

        for (CurriculumCompareScanResult ccsr : result) {
            if(ccsr.getEsoModel()!=null && ccsr.getMineModel()!=null) {
                System.out.println("both: " + ccsr.getEsoModel().getName());
            }else if(ccsr.getEsoModel()!=null) {
                System.out.println("eso: " + ccsr.getEsoModel().getName());
            }else if(ccsr.getMineModel()!=null) {
                System.out.println("mine: "+ccsr.getMineModel().getName());
            }else{
                System.out.println("Что ты такое?");
            }
        }
    }
}
