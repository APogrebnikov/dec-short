package org.edec.newOrder.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolComissionerModel {
    private String fio, groupname, shortFio, role;

    public static String getFIOByRole(List<ProtocolComissionerModel> list, String role) {
        String fio = "";
        for (ProtocolComissionerModel model : list) {
            if(model.getRole().toLowerCase().equals(role.toLowerCase())) {
                fio = model.getFio();
                switch (role.toLowerCase()) {
                    case "председатель комиссии":
                        list.remove(model);
                        break;
                    case "профсоюзная организация":
                        list.remove(model);
                    break;
                    case "совет обучающихся":
                        list.remove(model);
                    break;
                }
                return fio;
            }
        }
        return fio;
    }
}
