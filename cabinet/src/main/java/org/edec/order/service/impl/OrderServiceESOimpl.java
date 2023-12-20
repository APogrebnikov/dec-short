package org.edec.order.service.impl;

import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.OrderModel;
import org.edec.order.model.OrderStatusModel;
import org.edec.order.model.OrderTypeModel;
import org.edec.order.service.OrderService;
import org.edec.utility.fileManager.FileManager;

import java.io.File;
import java.util.List;


public class OrderServiceESOimpl implements OrderService {
    private EntityManagerOrderESO emESO = new EntityManagerOrderESO();
    private CreateOrderManagerESO comESO = new CreateOrderManagerESO();

    @Override
    public List<OrderModel> getOrderArchiveByFilter (Long idInst, int formOfStudy, Long idStatus, Long idType, String fioStudent) {
        return emESO.getOrderByFilter(idInst, formOfStudy, idStatus, idType, fioStudent, null);
    }

    @Override
    public List<OrderTypeModel> getDistinctType () {
        return emESO.getDistinctOrderType();
    }

    @Override
    public List<OrderStatusModel> getAllStatus () {
        return emESO.getAllStatus();
    }

    @Override
    public void deleteOrder (OrderModel order) throws Exception {
        emESO.deleteOrderWithId(order.getIdOrder());

        if (order.getUrl() != null && !order.getUrl().equals("")) {
            FileManager fileManager = new FileManager();
            fileManager.deleteFolderWithFiles(new File(fileManager.getFullPath(order.getUrl())));
        }
    }

    @Override
    public boolean updateMarks () {
        long curSem = comESO.getCurrentSemester(1, 1);
        long prevSem = comESO.getPrevSemester(curSem);
        long prevPrevSem = comESO.getPrevSemester(prevSem);

        if (emESO.updateSessionresult(curSem) && emESO.updateSessionresult(prevSem) && emESO.updateSessionresult(prevPrevSem)) {
            return true;
        }

        return false;
    }
}
