package org.edec.rest.ctrl;

import lombok.experimental.UtilityClass;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

@UtilityClass
public class HttpClientUtil {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";

    public static String makeHttpRequest(String url, String method, List<NameValuePair> params, String jsonInput) {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            client.setParams(httpParams);
            HttpResponse response = null;
            switch (method) {
                case GET:
                    String paramString = URLEncodedUtils.format(params, "UTF-8");
                    url += "?" + paramString;
                    HttpGet httpGet = new HttpGet(url);
                    response = client.execute(httpGet);
                    break;
                case POST:
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader("content-type", "application/json");
                    httpPost.setHeader("accept-charset", "utf-8");
                    HttpEntity httpEntityPost = new StringEntity((jsonInput == null
                            ? ""
                            : jsonInput), "utf-8");
                    httpPost.setEntity(httpEntityPost);
                    response = client.execute(httpPost);
                    break;
                case PUT:
                    HttpPut httpPut = new HttpPut(url);
                    httpPut.setHeader("content-type", "application/json");
                    httpPut.setHeader("accept-charset", "utf-8");
                    HttpEntity httpEntityPut = new StringEntity(jsonInput, "utf-8");
                    httpPut.setEntity(httpEntityPut);
                    response = client.execute(httpPut);
                    break;
            }

            assert response != null;
            HttpEntity httpEntity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            client.close();
            return sb.toString();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] getFileByREST(String url, String method, JSONObject data) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        client.setParams(httpParams);
        HttpResponse response = null;
        switch (method) {
            case POST:
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("content-type", "application/json");
                httpPost.setHeader("accept-charset", "utf-8");
                HttpEntity httpEntityPost = new StringEntity(data.toString(), "utf-8");
                httpPost.setEntity(httpEntityPost);
                response = client.execute(httpPost);
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(url);
                httpPut.setHeader("content-type", "application/json");
                httpPut.setHeader("accept-charset", "utf-8");
                HttpEntity httpEntityPut = new StringEntity(data.toString(), "utf-8");
                httpPut.setEntity(httpEntityPut);
                response = client.execute(httpPut);
                break;
        }

        assert response != null;
        HttpEntity httpEntity = response.getEntity();
        byte[] buffer = new byte[(int) httpEntity.getContentLength()];
        httpEntity.getContent().read(buffer);
        return buffer;
    }

    public static InputStream getFileByUrl(String urlPath) {
        try {
            URL url = new URL(urlPath);
            return url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}