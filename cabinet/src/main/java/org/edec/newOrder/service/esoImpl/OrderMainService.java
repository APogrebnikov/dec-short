package org.edec.newOrder.service.esoImpl;

import org.edec.newOrder.manager.OrderMainManagerESO;
import org.edec.newOrder.model.OrderRuleFilterModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.OrderStatusModel;
import org.edec.newOrder.model.OrderTypeModel;
import org.edec.utility.fileManager.FileManager;

import java.io.File;
import java.util.List;

public class OrderMainService {
    private OrderMainManagerESO mainManagerESO = new OrderMainManagerESO();

    public List<OrderEditModel> getOrderArchiveByFilter (Long idInst, int formOfStudy, Long idStatus, Long idType, String fioStudent, Long idOrderRule) {
        return mainManagerESO.getOrderByFilter(idInst, formOfStudy, idStatus, idType, fioStudent, null, idOrderRule);
    }

    public OrderEditModel getOrderById(Long id) {
        return mainManagerESO.getOrderById(id);
    }

    public List<OrderTypeModel> getOrderTypeList() {
        return mainManagerESO.getOrderTypeList();
    }

    public List<OrderRuleFilterModel> getOrderRuleFilter() {
        return mainManagerESO.getOrderRuleFilter();
    }

    public List<OrderStatusModel> getAllStatus () {
        return mainManagerESO.getAllStatus();
    }

    public void deleteOrder (OrderEditModel order) throws Exception {
        mainManagerESO.deleteOrderWithId(order.getIdOrder());

        if (order.getUrl() != null && !order.getUrl().equals("")) {
            FileManager fileManager = new FileManager();
            fileManager.deleteFolderWithFiles(new File(fileManager.getFullPath(order.getUrl())));
        }
    }

    public boolean updateMarks () {
        // TODO
        /*long curSem = comESO.getCurrentSemester(1, 1);
        long prevSem = comESO.getPrevSemester(curSem);

        if(emESO.updateSessionresult(curSem) && emESO.updateSessionresult(prevSem)) {
            return true;
        }*/

        return false;
    }
}
