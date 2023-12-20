package org.edec.utility.report.service.commission;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.secretaryChair.manager.EntityManagerSecretaryChair;
import org.edec.student.recordBook.model.StudentSemesterModel;
import org.edec.teacher.manager.EntityManagerCompletion;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.utility.report.manager.CommissionOrderDAO;
import org.edec.utility.report.model.commission.ListProtocolModel;
import org.edec.utility.report.model.commission.ProtocolModel;
import org.edec.utility.report.model.commission.ScheduleChairModel;
import org.edec.utility.report.model.commission.ScheduleSubjectModel;
import org.edec.utility.report.model.commission.dao.CommissionScheduleEso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommissionReportService {
    private EntityManagerCompletion emCompletion = new EntityManagerCompletion();
    private CommissionOrderDAO commissionOrderDAO = new CommissionOrderDAO();

    public JRBeanCollectionDataSource getListProtocol(Long idRegister) {
        List<EmployeeModel> employees = emCompletion.getEmployeeByCommission(idRegister);
        List<EmployeeModel> commission = new ArrayList<>();
        String fioChairman = null;
        for (EmployeeModel employeeModel : employees) {
            if (employeeModel.getChairman()) {
                fioChairman = employeeModel.getFio();
            } else {
                commission.add(employeeModel);
            }
        }

        List<ProtocolModel> list = commissionOrderDAO.getProtocols(idRegister);

        for (ProtocolModel protocol : list) {
            protocol.setChairman(fioChairman);
            protocol.setCommission(commission);
            protocol.setFullCommission(employees);
        }

        List<ListProtocolModel> data = new ArrayList<>();
        ListProtocolModel listProtocolModel = new ListProtocolModel();
        listProtocolModel.setProtocols(list);
        data.add(listProtocolModel);
        return new JRBeanCollectionDataSource(data);
    }

    public JRBeanCollectionDataSource getRegister(CommissionModel commission) {
        return new JRBeanCollectionDataSource(Collections.singletonList(commission));
    }

    public JRBeanCollectionDataSource getSchedule(Integer formOfStudy, Long idChair, Boolean printGroup, Boolean printMemebers) {
        List<CommissionScheduleEso> modelList = commissionOrderDAO.getScheduleModel(formOfStudy, idChair);
        /**В прогрессе - вывод студентов у дублей*/
       /*
        EntityManagerSecretaryChair emSecretary = new EntityManagerSecretaryChair();
        for (CommissionScheduleEso modelPre : modelList) {
            //if(modelPre.getIsIndividual()){
                List<StudentModel> listStudent = emSecretary.getStudentsByCommission(modelPre.getIdRegisterComission());
                String gn = modelPre.getGroupname();
                for (StudentModel studentModel : listStudent) {
                    if (gn.equals(studentModel.getGroupname())) {
                        modelPre.setGroupname(modelPre.getGroupname() + "\n" + studentModel.getShortFio());
                    }
                }
                if (listStudent.size()>0) {

                }
            //}
        }
        */
        //группировка по комиссиям
        List<CommissionScheduleEso> groupedModelList = new ArrayList<>();

        for(CommissionScheduleEso model : modelList){
            model.getGroups().add(model.getGroupname());
            boolean findCommision = false;
            for (CommissionScheduleEso commissionModel : groupedModelList) {
                if(model.getDatecommission()!=null
                        && model.getSubjectname()!=null
                        && model.getClassroom()!=null
                        && model.getFulltitle()!=null) {
                    if(model.getSubjectname().equals(commissionModel.getSubjectname())
                            && model.getDatecommission().equals(commissionModel.getDatecommission())
                            && model.getClassroom().equals(commissionModel.getClassroom())
                            && model.getFulltitle().equals(commissionModel.getFulltitle())){
                        boolean find = false;
                        for (String group : commissionModel.getGroups()) {
                            if(group.equals(model.getGroupname())) {
                                find = true;
                            }
                        }
                        if(!find) {
                            commissionModel.getGroups().add(model.getGroupname());
                            commissionModel.setGroupname(commissionModel.getGroupname() + "\n" + model.getGroupname());
                        }
                        findCommision = true;
                    }
                }
            }
            if(!findCommision){
                groupedModelList.add(model);
            }
        }

        List<ScheduleChairModel> chairs = new ArrayList<>();
        for (CommissionScheduleEso model : groupedModelList) {
            boolean addChair = true;
            for (ScheduleChairModel chair : chairs) {
                if (chair.getFulltitle().equals(model.getFulltitle())) {
                    ScheduleSubjectModel subject = new ScheduleSubjectModel();
                    subject.setClassroom(model.getClassroom());
                    subject.setDatecommission(model.getDatecommission());
                    subject.setSubjectname(model.getSubjectname());
                    subject.setGroups(model.getGroupname());
                    subject.setTeachers(model.getTeachers());
                    chair.getSubjects().add(subject);
                    chair.setPrintGroups(printGroup);
                    chair.setPrintMembersComm(printMemebers);
                    addChair = false;
                    break;
                }
            }
            if (addChair) {
                ScheduleChairModel chair = new ScheduleChairModel();
                chair.setFulltitle(model.getFulltitle());
                ScheduleSubjectModel subject = new ScheduleSubjectModel();
                subject.setClassroom(model.getClassroom());
                subject.setDatecommission(model.getDatecommission());
                subject.setSubjectname(model.getSubjectname());
                subject.setGroups(model.getGroupname());
                subject.setTeachers(model.getTeachers());
                chair.getSubjects().add(subject);
                chair.setPrintGroups(printGroup);
                chair.setPrintMembersComm(printMemebers);
                chairs.add(chair);
            }
        }
        return new JRBeanCollectionDataSource(chairs);
    }
}
