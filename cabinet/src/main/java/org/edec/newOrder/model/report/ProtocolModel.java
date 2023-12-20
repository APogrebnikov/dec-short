package org.edec.newOrder.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolModel {
    private String dateOfBegin, dateOfEnd, dateProtocol, protocolNumber, finalAmount;
    private List<ProtocolComissionerModel> comissionerList = new ArrayList<>();
    private List<ProtocolStudentModel> studentList = new ArrayList<>();
    private List<OrderCreateStudentModel> refusalStudentList = new ArrayList<>();
    private List<ProtocolStudentModel> totalStudentList = new ArrayList<>();
    private String predFIO, profFIO, sovetFIO;

    public List<ProtocolStudentModel> genTotalStudentList() {
        List<ProtocolStudentModel> total = new ArrayList<>();
        if (this.getRefusalStudentList() != null) {
            for (OrderCreateStudentModel orderCreateStudentModel : this.getRefusalStudentList()) {
                ProtocolStudentModel localSt = new ProtocolStudentModel();
                localSt.setFio(orderCreateStudentModel.getFio());
                localSt.setGroupname(orderCreateStudentModel.getGroupname());
                total.add(localSt);
            }
        }
        for (ProtocolStudentModel protocolStudentModel : this.studentList) {
            ProtocolStudentModel localSt = new ProtocolStudentModel();
            localSt.setFio(protocolStudentModel.getFio());
            localSt.setGroupname(protocolStudentModel.getGroupname());
            total.add(localSt);
        }
        return total;
    }
}
