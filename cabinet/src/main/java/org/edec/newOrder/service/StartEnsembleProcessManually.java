package org.edec.newOrder.service;

import lombok.extern.log4j.Log4j;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.EmployeeOrderModel;
import org.edec.order.model.OrderModel;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

@Log4j
public class StartEnsembleProcessManually {

    private EntityManagerOrderESO emManagerEso = new EntityManagerOrderESO();

    public boolean sendOrderToEnsembleWithoutCreatingFile(Long idOrder) {
        try {
            OrderModel order = emManagerEso.getOrderById(idOrder);
            OrderStatusConst orderStatus = OrderStatusConst.getOrderStatusConstByName(order.getStatus());
            /*switch (orderStatus) {
                case CREATED:
                case REVISION:
                    break;
                default:
                    return false;
            }*/


            //TODO нужно понять как брать файлы из webapp->web-inf папки
            String path = "C:\\FileServer\\" + order.getUrl();

            JSONObject jsonData = new JSONObject();
            jsonData.put("subject", order.getDescription());
            jsonData.put("path", path);
            jsonData.put("orderid", order.getIdOrder());
            jsonData.put("type", "по личному составу студентов");
            jsonData.put("ordernumber", order.getNumber());
            jsonData.put("lotusid", order.getIdLotus());

            EmployeeOrderModel approval = emManagerEso.getApprovalModel(idOrder);

            jsonData.put("listHum", new JSONArray()
                    .put(new JSONObject()
                            .put("fio", approval.getFio())
                            .put("iter", 1)
                            .put("idhum", approval.getIdHum())
                            .put("email", approval.getEmail())
                            .put("sign", 2)));
            jsonData.put("listDistribution", new JSONArray());
            jsonData.put("listExecutor", new JSONArray());

            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(
                            "http://193.218.136.174:57772/orders2/orderinput/start",
                            HttpClient.POST,
                            new ArrayList<>(),
                            jsonData.toString()
                    ));
            System.out.println(jsonData.toString());
            return jsonObject.has("Status");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Не удалось отправить на согласование приказ " + idOrder);
            return false;
        }
    }
}
