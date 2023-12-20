package org.edec.newOrder.service.orderCreator.concept;

import org.edec.newOrder.model.ScholarshipModel;

import java.util.List;

public class ParamsGetter {
    public String getDescFromParams (List<Object> params) {
        return (String) params.get(params.size() - 1);
    }

    public Long getIdSemesterFromParams(List<Object> params) {
        return (Long) params.get(params.size() - 2);
    }

    public Integer getFOSFromParams (List<Object> params) {
        return (Integer) params.get(params.size() - 3);
    }

    public Long getIdInstFromParams(List<Object> params) {
        return (Long) params.get(params.size() - 4);
    }

    public ScholarshipModel getScholarshipInfoFromParams(List<Object> params) {
        return (ScholarshipModel) params.get(params.size() - 5);
    }
}
