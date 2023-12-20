package org.edec.workflow.model;

import org.edec.utility.Timing;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmmax
 */
public class WorkflowModel {
    public static final Integer ACTION_PREVIEW_AND_UNSIGN = 0;
    public static final Integer ACTION_PREVIEW_AND_UNSIGN_AND_SIGN = 1;
    public static final Integer ACTION_PREVIEW_AND_UNSIGN_AND_SIGN_FINAL = 2;

    public static final String STATUS_SIGNED_EN = "signed";
    public static final String STATUS_UNSIGNED_EN = "unsigned";
    public static final String STATUS_SIGNED_RUS = "Подтверждена";
    public static final String STATUS_UNSIGNED_RUS = "Отклонена";

    private Boolean isNew;
    private Boolean statusUpdated;

    private Long orderId;

    private Integer priority;
    private Integer taskId;
    private Integer signReq;

    private String subject;
    private String message;
    private String timeCreated;
    private String timeComplited;
    private String age;
    private String certNumber;
    private String fio = "";
    private String lotusID;
    private String pathFile;
    private String operation;
    private String orderType;
    private String orderNumber;
    private String action;
    private String reason;

    private String sessionId;
    private String statusTask;

    private String status;

    private Map<String, String> mapActions;

    public WorkflowModel () {
        this.setIsNew(false);
        this.setStatusUpdated(false);
    }

    public WorkflowModel (JSONObject obj) {
        if (obj.has("New")) {
            this.parsIsNew(obj.getString("New"));
        } else {
            this.setIsNew(false);
        }

        if (obj.has("Action")) {
            if (obj.getString("Action").equals(WorkflowModel.STATUS_SIGNED_EN)) {
                this.setStatus(STATUS_SIGNED_RUS);
            }
            if (obj.getString("Action").equals(WorkflowModel.STATUS_UNSIGNED_EN)) {
                this.setStatus(STATUS_UNSIGNED_RUS);
            }
        }
        if (obj.has("Actions")) {
            if (this.getIsNew()) {
                String actions[] = obj.getString("Actions").split(",");

                mapActions = new HashMap<>();
                for (String action1 : actions) {
                    this.mapActions.put(action1, action1);
                }
            }
        }
        if (obj.has("Age")) {
            if (obj.get("Age") != null) {
                if (obj.get("Age").toString().equals("")) {
                    this.age = "-";
                } else {
                    Integer ageLocal = null;
                    try {
                        ageLocal = obj.getInt("Age");
                    } catch (Exception e) {
                    }
                    this.age = ageLocal == null ? "" : Timing.getRusTimestampByMinutes(ageLocal.longValue());
                }
            }
        }

        if (obj.has("Fio")) {
            fio = obj.getString("Fio");
        }
        if (obj.has("LotusID")) {
            lotusID = obj.getString("LotusID");
        }
        if (obj.has("Message")) {
            this.setMessage(obj.getString("Message"));
        }
        if (obj.has("Operation")) {
            this.operation = obj.getString("Operation");
        }
        if (obj.has("OrderId")) {
            this.setOrderId(obj.getLong("OrderId"));
        }
        if (obj.has("OrderNumber")) {
            this.setOrderNumber(obj.getString("OrderNumber"));
        }
        if (obj.has("OrderType")) {
            this.setOrderType(obj.getString("OrderType"));
        }
        if (obj.has("Path")) {
            this.setPathFile(obj.getString("Path"));
        }
        if (obj.has("Priority")) {
            this.parsPriority(obj.get("Priority").toString());
        }
        if (obj.has("Reason") && obj.get("Reason") != null) {
            this.reason = obj.get("Reason").toString();
        }
        if (obj.has("CertNumber") && obj.get("CertNumber") != null) {
            this.certNumber = obj.get("CertNumber").toString();
        }
        if (obj.has("SessionId")) {
            setSessionId(obj.get("SessionId").toString());
        }
        if (obj.has("SignReq")) {
            if (obj.get("SignReq").toString().equals(WorkflowModel.ACTION_PREVIEW_AND_UNSIGN.toString())) {
                signReq = WorkflowModel.ACTION_PREVIEW_AND_UNSIGN;
            } else if (obj.get("SignReq").toString().equals(WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN.toString())) {
                signReq = WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN;
            } else if (obj.get("SignReq").toString().equals(WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN_FINAL.toString())) {
                signReq = WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN_FINAL;
            }
        }
        if (obj.has("Status")) {
            this.parsStatusUpdated(obj.getString("Status"));
        }
        if (obj.has("StatusTask")) {
            parsStatusTask(obj.getString("StatusTask"));
        }
        if (obj.has("Subject")) {
            this.setSubject(obj.getString("Subject"));
        }
        if (obj.has("TaskId") && obj.get("TaskId") != null) {
            this.parsTaskId(obj.get("TaskId").toString());
        }
        if (obj.has("TimeCreated")) {
            this.setTimeCreated(obj.getString("TimeCreated").split("\\.")[0]);
        }
        if (obj.has("TimeComplited")) {
            this.setTimeComplited(obj.getString("TimeComplited").split("\\.")[0]);
        }
    }

    /**
     * @return the isNew
     */
    public Boolean getIsNew () {
        return isNew;
    }

    /**
     * @param isNew the isNew to set
     */
    public void setIsNew (Boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * @param isNew the isNew to set
     */
    public void parsIsNew (String isNew) {
        if (isNew != null && isNew.length() > 0) {
            this.isNew = true;
        } else {
            this.isNew = false;
        }
    }

    /**
     * @return the priority
     */
    public Integer getPriority () {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority (Integer priority) {
        this.priority = priority;
    }

    /**
     * @param priority the priority to set
     */
    public void parsPriority (String priority) {
        this.priority = Integer.parseInt(priority);
    }

    /**
     * @return the subject
     */
    public String getSubject () {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject (String subject) {
        this.subject = subject;
    }

    /**
     * @return the message
     */
    public String getMessage () {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage (String message) {
        this.message = message;
    }

    /**
     * @return the taskId
     */
    public Integer getTaskId () {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId (Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void parsTaskId (String taskId) {
        this.taskId = Integer.parseInt(taskId);
    }

    /**
     * @return the timeCreated
     */
    public String getTimeCreated () {
        return timeCreated;
    }

    /**
     * @param timeCreated the timeCreated to set
     */
    public void setTimeCreated (String timeCreated) {
        this.timeCreated = timeCreated;
    }

    /**
     * @return the timeComplited
     */
    public String getTimeComplited () {
        return timeComplited;
    }

    /**
     * @param timeComplited the timeComplited to set
     */
    public void setTimeComplited (String timeComplited) {
        this.timeComplited = timeComplited;
    }

    /**
     * @return the age
     */
    public String getAge () {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge (String age) {
        this.age = age;
    }

    private void parsStatusTask (String statusEng) {
        if (statusEng == null) {
            this.statusTask = "";
        } else if (statusEng.equals("Canceled")) {
            this.statusTask = "Отменена";
        } else if (statusEng.equals("Assigned")) {
            this.statusTask = "В процессе";
        } else if (statusEng.equals("Completed")) {
            this.statusTask = "Подтверждена";
        } else {
            this.statusTask = "";
        }
    }

    /**
     * @return the pathFile
     */
    public String getPathFile () {
        return pathFile;
    }

    /**
     * @param pathFile the pathFile to set
     */
    public void setPathFile (String pathFile) {
        this.pathFile = pathFile;
    }

    /**
     * @return the orderType
     */
    public String getOrderType () {
        return orderType;
    }

    /**
     * @param orderType the orderType to set
     */
    public void setOrderType (String orderType) {
        this.orderType = orderType;
    }

    /**
     * @return the orderNumber
     */
    public String getOrderNumber () {
        return orderNumber;
    }

    /**
     * @param orderNumber the orderNumber to set
     */
    public void setOrderNumber (String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * @return the status
     */
    public String getStatus () {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus (String status) {
        this.status = status;
    }

    /**
     * @return the action
     */
    public String getAction () {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction (String action) {
        this.action = action;
    }

    /**
     * @return the reason
     */
    public String getReason () {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason (String reason) {
        this.reason = reason;
    }

    /**
     * Возвращает набор имен доступных дейсвтий.
     *
     * @return набор действий.
     */
    public Map<String, String> getMapActions () {
        return this.mapActions;
    }

    /**
     * @return the signReq
     */
    public Integer getSignReq () {
        return signReq;
    }

    /**
     * @param signReq the signReq to set
     */
    public void setSignReq (Integer signReq) {
        this.signReq = signReq;
    }

    public Long getOrderId () {
        return orderId;
    }

    public void setOrderId (Long orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the statusUpdated
     */
    public Boolean getStatusUpdated () {
        return statusUpdated;
    }

    /**
     * @param statusUpdated the statusUpdated to set
     */
    public void setStatusUpdated (Boolean statusUpdated) {
        this.statusUpdated = statusUpdated;
    }

    /**
     * @param statusUpdated the statusUpdated to set
     */
    public void parsStatusUpdated (String statusUpdated) {
        if (statusUpdated == null || statusUpdated.equals("Error")) {
            this.statusUpdated = false;
        } else if (statusUpdated.equals("OK")) {
            this.statusUpdated = true;
        }
    }

    public String getSessionId () {
        return sessionId;
    }

    public void setSessionId (String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getCertNumber () {
        return certNumber;
    }

    public void setCertNumber (String certNumber) {
        this.certNumber = certNumber;
    }

    public String getStatusTask () {
        return statusTask;
    }

    public void setStatusTask (String statusTask) {
        this.statusTask = statusTask;
    }

    public String getOperation () {
        return operation;
    }

    public void setOperation (String operation) {
        this.operation = operation;
    }

    public String getLotusID () {
        return lotusID;
    }

    public void setLotusID (String lotusID) {
        this.lotusID = lotusID;
    }
}
