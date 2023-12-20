package org.edec.dynamicReport.service;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by lunin on 02.04.2018.
 */
public interface DynamicReportService {

    String getListMeasures (); //Получить список показателей

    String getListQualityMeasures (); //Получить список вычисляемых показателей

    String getListFilters (); //Получить список фильтров

    JSONObject MDXQuery (JSONObject object);
}

