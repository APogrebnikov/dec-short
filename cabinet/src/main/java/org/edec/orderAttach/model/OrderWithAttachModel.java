package org.edec.orderAttach.model;

import lombok.*;
import org.edec.commons.model.OrderAttachModel;
import org.edec.commons.model.OrderModel;
import org.edec.orderAttach.service.SimpleListener;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OrderWithAttachModel extends OrderModel {
    private SignedStatus signedStatus = SignedStatus.NONE_SIGNED;

    private List<OrderAttachModel> orderAttaches = new ArrayList<>();
    private List<SimpleListener> listeners = new ArrayList<>();

    public void addAttach(OrderAttachModel orderAttach) {
        orderAttaches.add(orderAttach);
    }

    public void addListener(SimpleListener simpleListener) {
        listeners.add(simpleListener);
    }
    public void clearListeners() {
        listeners.clear();
    }

    public void checkStatus() {
        int countSigned = 0;
        for (OrderAttachModel orderAttach : orderAttaches) {
            if (orderAttach.getCertNumber() != null) {
                ++countSigned;
            }
        }
        SignedStatus tmpStatus;
        if (countSigned == 0) {
            tmpStatus = SignedStatus.NONE_SIGNED;
        } else if (countSigned == orderAttaches.size()) {
            tmpStatus = SignedStatus.ALL_SIGNED;
        } else {
            tmpStatus = SignedStatus.SOME_SIGNED;
        }
        this.signedStatus = tmpStatus;
        notifyListeners();
    }

    private void notifyListeners() {
        for (SimpleListener listener : listeners) {
            listener.fireEvent();
        }
    }

    @AllArgsConstructor
    @Getter
    public enum SignedStatus {
        ALL_SIGNED("Подписаны все"),
        SOME_SIGNED("Подписана часть"),
        NONE_SIGNED("Не подписано не одна");

        private String statusName;
    }
}
