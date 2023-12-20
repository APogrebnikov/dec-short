package org.edec.rest.ctrl;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.edec.main.auth.AuthLDAP;
import org.edec.main.model.UserModel;
import org.edec.main.service.UserService;
import org.edec.main.service.impl.UserServiceESOimpl;
import org.edec.rest.manager.LoginRestDAO;
import org.edec.rest.model.LoginMsg;
import org.edec.rest.model.UserMsg;
import org.edec.utility.zk.PopupUtil;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/auth")
public class LoginCtrl {
    private static final Logger log = Logger.getLogger(LoginCtrl.class.getName());
    LoginRestDAO loginRestDAO = new LoginRestDAO();
    private AuthLDAP authLDAP = new AuthLDAP();
    private UserService userService = new UserServiceESOimpl();

    /**
     * Авторизация и создание пользовательского токена
     * @param loginMsg
     * @return {msg:"", status:"", usertoken:""}
     */
    @POST
    @Path("/login")
    @Consumes("application/json;charset=utf-8")
    public Response login (LoginMsg loginMsg) {
        JSONObject jsonObject = new JSONObject();
        String user = loginRestDAO.checkToken(loginMsg.getAppToken());
        if (user != "") {
            log.debug("app "+ user + " login");
           // jsonObject.append("msg", "Hello " + user);
        }else{
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status","error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        }


        // Авторизация родителей
        if (loginMsg.getUsername().startsWith("PA_")) {
            UserModel userModel = userService.getUserByInnerLogin(loginMsg.getUsername(), loginMsg.getPassword());
            if (userModel != null) {
                String userToken = loginRestDAO.startUserSession(loginMsg.getUsername(), 5);
                jsonObject.put("usertoken", userToken);
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            } else {
                jsonObject.put("msg", "User not from ISIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_FORBIDDEN);
            }
        }

        // Авторизация стандартная
        String cn = null;
        try {
            cn = authLDAP.getCN(loginMsg.getUsername(), loginMsg.getPassword());
        } catch (Exception e) {
            jsonObject.put("msg", "Miss LDAP connection");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        if ((cn == null || loginMsg.getPassword().equals(""))
                && !loginMsg.getUsername().equals("APogrebnikov") // Заглушка для тестирования студентами
        ) {
            jsonObject.put("msg", "Can't login");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString());
        } else {
            UserModel userModel = userService.getUserByLdapLogin(loginMsg.getUsername());
            if (userModel != null) {
                String userToken = loginRestDAO.startUserSession(loginMsg.getUsername(), 5);
                jsonObject.put("usertoken", userToken);
                jsonObject.put("status", "success");
            } else {
                jsonObject.put("msg", "User not from ISIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_FORBIDDEN);
            }
        }
        return prepareResponse(jsonObject.toString());
    }

    /**
     * Проверка пользовательского токена
     * @param userMsg
     * @return {msg:"", status:""}
     */
    @POST
    @Path("/test")
    @Consumes("application/json;charset=utf-8")
    public Response test (UserMsg userMsg) {
        String un = loginRestDAO.checkUserToken(userMsg.getUserToken());

        JSONObject jsonObject = new JSONObject();

        if (un == "") {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            jsonObject.put("msg", "Hello "+un);
            jsonObject.put("status", "success");
        }

        return prepareResponse(jsonObject.toString());
    }

    public boolean checkToken(String token){
        String un = loginRestDAO.checkUserToken(token);
        if (un != "") {
            return true;
        }
        return false;
    }

    /**Секция костылей для CORS**/
    @OPTIONS
    @Path("/login")
    public Response loginOpt(){
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/test")
    public Response testOpt(){
        return prepareResponse("");
    }
    /**Конец секции костылей для CORS**/

    public Response prepareResponse(String res) {
        return prepareResponse(res, HttpStatus.SC_OK);
    }

    public Response prepareResponse(String res, int status) {
        Response.ResponseBuilder response = Response.status(status);
        response.entity(res);
        response.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        return response.build();
    }
}
