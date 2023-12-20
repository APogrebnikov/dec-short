package org.edec.commission.report.model.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax
 */
public class ListProtocolModel {
    private List<ProtocolModel> protocols = new ArrayList<>();

    public ListProtocolModel () {
    }

    public List<ProtocolModel> getProtocols () {
        return protocols;
    }

    public void setProtocols (List<ProtocolModel> protocols) {
        this.protocols = protocols;
    }
}
