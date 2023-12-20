package org.edec.rest.ctrl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EsoService {
    //конфигурация
    private String username="webservice";
    private String password="789hgds345.rte";

    //переменные
    private String token;
    private Pattern patternSearchPercentage = Pattern.compile("[\\(]?(\\d+(\\.\\d{1,2})?)%[\\)]?");

    public void generateToken() {
        List<NameValuePair> paramsAuth = new ArrayList<>();
        paramsAuth.add(new BasicNameValuePair("username", username));
        paramsAuth.add(new BasicNameValuePair("password", password));
        paramsAuth.add(new BasicNameValuePair("service", "el_dean"));
        JSONObject jsonToken = new JSONObject(
                HttpClientUtil.makeHttpRequest(
                        "https://e.sfu-kras.ru/login/token.php",
                        HttpClientUtil.GET,
                        paramsAuth, ""));
        token = jsonToken.getString("token");
    }

    public JSONArray getJSONGradesForStudent(Long idEsoStudent, Long idEsoCourse) {
        List<NameValuePair> paramsData = new ArrayList<>();
        paramsData.add(new BasicNameValuePair("courseid", String.valueOf(idEsoCourse)));
        paramsData.add(new BasicNameValuePair("userid", String.valueOf(idEsoStudent)));
        paramsData.add(new BasicNameValuePair("moodlewsrestformat", "json"));
        paramsData.add(new BasicNameValuePair("wstoken", token));
        paramsData.add(new BasicNameValuePair("wsfunction", "local_core_course_grades"));
        String res = HttpClientUtil.makeHttpRequest("https://e.sfu-kras.ru/webservice/rest/server.php", HttpClientUtil.GET, paramsData, null);
        JSONArray result = new JSONArray(res);
        return result;
    }
}
