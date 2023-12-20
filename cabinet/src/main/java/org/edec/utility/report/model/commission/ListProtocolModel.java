package org.edec.utility.report.model.commission;

import java.util.ArrayList;
import java.util.List;


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
