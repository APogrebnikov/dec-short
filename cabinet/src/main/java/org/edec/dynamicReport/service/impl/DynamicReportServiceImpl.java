package org.edec.dynamicReport.service.impl;

import com.lowagie.text.ExceptionConverter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.edec.dynamicReport.service.DynamicReportService;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lunin on 02.04.2018.
 */
public class DynamicReportServiceImpl implements DynamicReportService {

    private static final String URL = "http://localhost:57772/api/deepsee/v1/cube";
    private static final String URL_GET_LIST_MEASURES = URL + "/Info/Measures/StudentRaiting"; //Получить список показателей
    private static final String URL_GET_LIST_Q_MEASURES = URL + "/Info/QualityMeasures/StudentRaiting"; //Получить список показателей
    private static final String URL_GET_LIST_FILTERS = URL + "/Info/Filters/StudentRaiting"; //Получить список показателей
    private static final String URL_MDX_QUERY = URL + "/MDXExecute"; //Получить список показателей

    @Override
    public String getListMeasures () {
        List<NameValuePair> params = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(HttpClient.makeHttpRequest(URL_GET_LIST_MEASURES, HttpClient.POST, params, null));
            if (!jsonObject.getJSONObject("Info").optString("Error").equals("")) {
                return "Error";
            } else {
                JSONArray jArray = jsonObject.getJSONObject("Result").getJSONArray("Measures");
                return jArray.toString();
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    @Override
    public String getListQualityMeasures () {
        List<NameValuePair> params = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(HttpClient.makeHttpRequest(URL_GET_LIST_Q_MEASURES, HttpClient.POST, params, null));
            if (!jsonObject.getJSONObject("Info").optString("Error").equals("")) {
                return "Error";
            } else {
                JSONArray jArray = jsonObject.getJSONObject("Result").getJSONArray("QualityMeasures");
                return jArray.toString();
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    @Override
    public String getListFilters () {
        List<NameValuePair> params = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(HttpClient.makeHttpRequest(URL_GET_LIST_FILTERS, HttpClient.POST, params, null));
            if (!jsonObject.getJSONObject("Info").optString("Error").equals("")) {
                return "Error";
            } else {
                JSONArray jArray = jsonObject.getJSONObject("Result").getJSONArray("Filters");
                return jArray.toString();
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    @Override
    public JSONObject MDXQuery (JSONObject object) {
        // Генерация MDX запроса
        String query = "SELECT NON EMPTY [Measures].[%COUNT] ON 1 FROM [STUDENTRAITING]";

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("MDX", query));

        String d = HttpClient.makeHttpRequest(URL_MDX_QUERY, HttpClient.POST, params, null);
        JSONObject jsonObject = new JSONObject(d);

        if (!jsonObject.getJSONObject("Info").optString("Error").equals("")) {
            throw new ExceptionConverter(new Exception(jsonObject.getJSONObject("Info").optString("Error")));
        } else {
            return jsonObject.getJSONObject("Result");
            // Для тестовой версии, пока нет генерации MDX запроса
        }
    }
}
