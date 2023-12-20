package org.edec.rest.ctrl;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.edec.notificationCenter.manager.NotificationManager;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.rest.manager.LoginRestDAO;
import org.edec.rest.model.UserMsg;
import org.edec.rest.model.student.request.NotificationStatusMsg;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/notification")
public class NotificationCtrl {
    private static final Logger log = Logger.getLogger(NotificationCtrl.class.getName());
    private NotificationManager notificationManager = new NotificationManager();
    LoginRestDAO loginRestDAO = new LoginRestDAO();

    /**
     * Получение системных уведомлений
     *
     * @param userMsg
     */
    @POST
    @Path("/systemNotificationByUser")
    @Consumes("application/json;charset=utf-8")
    public Response getSystemNotificationByHum(UserMsg userMsg) {
        Long id_humanface = loginRestDAO.getHumanfaceByToken(userMsg.getUserToken());
        if (id_humanface != null) {
            List<NotificationModel> notifications = notificationManager.getUserNotifications(id_humanface);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "no notifications");
            if (notifications.isEmpty()) {
                jsonObject.put("msg", "no notifications");
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            } else {
                JSONArray jsonArray = new JSONArray(notifications);
                jsonObject.put("notifications", jsonArray);
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "no user found");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение кол-ва непрочитанных
     *
     * @param userMsg
     */
    @POST
    @Path("/count")
    @Consumes("application/json;charset=utf-8")
    public Response getSystemNotificationCount(UserMsg userMsg) {
        Long id_humanface = loginRestDAO.getHumanfaceByToken(userMsg.getUserToken());
        if (id_humanface != null) {
            Integer countUnread = notificationManager.getUserNotificationsCount(id_humanface);

            JSONObject jsonObject = new JSONObject();

            if (countUnread == null) {
                jsonObject.put("unreadCount", 0);
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            } else {
                jsonObject.put("unreadCount", countUnread);
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        }
    }

    @POST
    @Path("/systemNotification")
    @Consumes("application/json;charset=utf-8")
    public Response getSystemNotification(UserMsg userMsg) {
        Long id_humanface = loginRestDAO.getHumanfaceByToken(userMsg.getUserToken());
        if (id_humanface != null) {
            List<NotificationModel> notifications = notificationManager.getUserNotifications(id_humanface);

            JSONObject jsonObject = new JSONObject();
            if (notifications.isEmpty()) {
                jsonObject.put("msg", "no notifications");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            } else {
                JSONArray jsonArray = new JSONArray(notifications);
                jsonObject.put("notifications", jsonArray);
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        }
    }

    @POST
    @Path("/markRead")
    @Consumes("application/json;charset=utf-8")
    public Response setReadNotification(NotificationStatusMsg notificationStatusMsg) {
        Long id_humanface = loginRestDAO.getHumanfaceByToken(notificationStatusMsg.getUserToken());
        if (id_humanface != null) {
            if (notificationManager.updateNotificationStatus(notificationStatusMsg.getNotifications(), id_humanface)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", "notifications updated");
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", "can't update notification");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        }
    }

    /**
     * Секция костылей для CORS
     **/
    @OPTIONS
    @Path("/systemNotificationByUser")
    public Response getSystemNotificationByHumOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/count")
    public Response getSystemNotificationCountOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/systemNotification")
    public Response getSystemNotificationOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/markRead")
    public Response setReadNotificationOpt() {
        return prepareResponse("");
    }

    /**
     * Конец секции костылей для CORS
     **/

    public Response prepareResponse(String res) {
        return prepareResponse(res, HttpStatus.SC_OK);
    }

    public Response prepareResponse(String res, int status) {
        Response.ResponseBuilder response = Response.status(status);
        response.entity(res);
        response.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        return response.build();
    }

}
