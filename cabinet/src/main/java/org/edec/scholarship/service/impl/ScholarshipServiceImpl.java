package org.edec.scholarship.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.scholarship.manager.ScholarshipManager;
import org.edec.scholarship.model.ScholarshipHistoryDTO;
import org.edec.scholarship.model.ScholarshipModel;
import org.edec.scholarship.service.ScholarshipService;
import org.edec.scholarship.model.ScholarshipHistoryModel;
import org.edec.utility.constants.ScholarshipTypeConst;
import org.edec.utility.converter.DateConverter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class ScholarshipServiceImpl implements ScholarshipService {

    private final ScholarshipManager scholarshipManager = new ScholarshipManager();

    @Override
    public Boolean updateScholarship(ScholarshipModel scholarship) {
        return scholarshipManager.updateScholarship(scholarship);
    }

    @Override
    public Boolean deleteScholarship(ScholarshipModel scholarship) {
        return scholarshipManager.deleteScholarship(scholarship);
    }

    @Override
    public List<ScholarshipModel> getScholarshipsByStudent(Long idStudentcard) {
        return convertJsonToScholarship(scholarshipManager.getScholarshipsByStudent(idStudentcard), idStudentcard);
    }

    @Override
    public List<ScholarshipHistoryModel> getScholarshipHistory(Long idStudentcard) {
        return scholarshipManager.getScholarshipHistory(idStudentcard).stream()
                                 .map(ScholarshipHistoryModel::new)
                                 .collect(Collectors.toList());
    }


    private List<ScholarshipModel> convertJsonToScholarship(String jsonScholarship, Long idStudentcard){

        List<ScholarshipModel> scholarshipModelList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonScholarship);

        List<ScholarshipTypeConst> scholarshipTypes = Arrays.asList(ScholarshipTypeConst.values());

        scholarshipTypes.forEach(scholarshipTypeConst -> fillScholarship(scholarshipModelList,
                                                                         scholarshipTypeConst,
                                                                         jsonObject,
                                                                         idStudentcard));

        return scholarshipModelList;
    }

    private void fillScholarship(List<ScholarshipModel> scholarshipModelList,
                                 ScholarshipTypeConst scholarshipTypeConst,
                                 JSONObject jsonObject,
                                 Long idStudentcard){

        ScholarshipModel scholarshipModel = new ScholarshipModel();
        scholarshipModel.setIdStudentCard(idStudentcard);

        JSONObject jsonScholarship = jsonObject.has(scholarshipTypeConst.getJsonName())
                                     ? jsonObject.getJSONObject(scholarshipTypeConst.getJsonName())
                                     : null;

        scholarshipModel.setType(scholarshipTypeConst);

        if(jsonScholarship != null){
            scholarshipModel.setDateFrom(DateConverter.convertStringToDate(jsonScholarship.get("dateFrom").toString(),"yyyy-MM-dd"));
            scholarshipModel.setDateTo(DateConverter.convertStringToDate(jsonScholarship.get("dateTo").toString(),"yyyy-MM-dd"));
        }

        scholarshipModelList.add(scholarshipModel);
    }
}
