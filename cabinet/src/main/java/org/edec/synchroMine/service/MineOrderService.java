package org.edec.synchroMine.service;

import org.edec.order.model.OrderModel;

import javax.swing.text.BadLocationException;
import java.io.IOException;

/**
 * @author Max Dimukhametov
 */
public interface MineOrderService {
    boolean createOrderInMine (OrderModel orderESO) throws IOException, BadLocationException;
}
