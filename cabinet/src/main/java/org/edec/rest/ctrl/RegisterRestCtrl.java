package org.edec.rest.ctrl;

import javafx.util.Pair;
import lombok.extern.java.Log;
import org.edec.main.service.RequestService;
import org.edec.main.service.impl.RequestServiceImpl;
import org.javatuples.Triplet;
import org.json.JSONArray;
import org.json.JSONObject;
import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@Path("/register")
@Singleton
@Log
public class RegisterRestCtrl {

    private RequestService requestService = new RequestServiceImpl();
    private Integer currentCountOfRegisterRequest = 0;

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    private Set<Triplet> counts;

    public RegisterRestCtrl() {
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 30000);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            counts = requestService.getAllOpenRequestCount();
            currentCountOfRegisterRequest = currentCountOfRegisterRequest + 1;
        }
    }

    @POST
    @Path("/request/count")
    @Consumes("application/json;charset=utf-8")
    public String getCountOfRegisterRequest(String json) {
        try {
            Integer count = 0;
            Integer personal;
            Set<Pair<Long, Integer>> curRights = parsRights(json);
            Long idHumanface = (new JSONObject(json).getLong("idHumanface"));
            Integer allNotificationsCount;


            if (counts != null) {
                for (Triplet rightInTriplet : counts) {
                    for (Pair<Long, Integer> userRight : curRights) {
                        if (rightInTriplet.getValue0().equals(userRight.getKey())
                                && rightInTriplet.getValue1().equals(userRight.getValue())) {
                            count += (Integer) rightInTriplet.getValue2();
                        }
                    }
                }
            }

            personal = requestService.getPersonalNotificationCount(idHumanface);

            allNotificationsCount = requestService.getAllNotificationCount(idHumanface);

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("count", count);
            jsonObject.put("personal", personal);

            if (new JSONObject(json).getBoolean("hasAccess")) {
                jsonObject.put("all", allNotificationsCount);
            }

            return jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count", 0);
            jsonObject.put("personal", 0);
            return jsonObject.toString();
        }
    }

    public Set<Pair<Long, Integer>> parsRights(String rights) {
        JSONObject inputJSON = new JSONObject(rights);
        JSONArray rightsJSON = new JSONArray(inputJSON.has("rights") ? inputJSON.get("rights").toString() : "");

        Set<Pair<Long, Integer>> curRights = new HashSet<>();

        for (int i = 0; i < rightsJSON.length(); i++) {
            JSONObject curRight = rightsJSON.getJSONObject(i);
            Integer foc = (Integer) curRight.get("value");
            Long idInstitute = Long.parseLong(curRight.get("key").toString());
            curRights.add(new Pair<>(idInstitute, foc));
        }

        return curRights;
    }
}
