package org.edec.synchroMine.model.eso;


public class StudentModel {
    private Long idStudentcard;
    private Long idSSS;
    private Long idMineStudentcard;
    private Integer idStatus;

    private String fio;
    private String recordbook;
    private String reasonStudy;

    public StudentModel () {

    }

    public Long getIdStudentcard () {
        return idStudentcard;
    }

    public void setIdStudentcard (Long idStudentcard) {
        this.idStudentcard = idStudentcard;
    }

    public Long getIdSSS () {
        return idSSS;
    }

    public void setIdSSS (Long idSSS) {
        this.idSSS = idSSS;
    }

    public Long getIdMineStudentcard () {
        return idMineStudentcard;
    }

    public void setIdMineStudentcard (Long idMineStudentcard) {
        this.idMineStudentcard = idMineStudentcard;
    }

    public Integer getIdStatus () {
        return idStatus;
    }

    public void setIdStatus (Integer idStatus) {
        this.idStatus = idStatus;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getReasonStudy () {
        return reasonStudy;
    }

    public void setReasonStudy (String reasonStudy) {
        this.reasonStudy = reasonStudy;
    }
}
