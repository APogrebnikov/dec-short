package org.edec.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

/**
 * Модель для прикрепленных файлов к приказу
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAttachModel {
    private Long idOrderAttach;

    private String certNumber, certFio;
    private String fileName;
    private String params;
    private String typeReport;

    private OrderModel order;

    public JSONObject getJSONData() {
        if (params != null) {
            return new JSONObject(params);
        }
        return null;
    }
}
