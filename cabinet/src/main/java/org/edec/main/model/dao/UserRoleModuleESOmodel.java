package org.edec.main.model.dao;


public class UserRoleModuleESOmodel {
    private boolean groupLeader = false;
    private boolean readonly = true;
    private boolean student = false;
    private boolean teacher = false;
    private boolean parent = false;

    private Integer formofstudy;
    private Integer formofstudystudent;
    private Integer type;

    private Long idChair;
    private Long idDepartment;
    private Long idHum;
    private Long idInstitute;
    private Long idInstituteMine;
    private Long idModule;
    private Long idParent;

    private String fio;
    private String fulltitle;
    private String groupname;
    private String imagePath;
    private String institute;
    private String moduleName;
    private String roleName;
    private String shorttitle;
    private String startPage;
    private String url;

    private Integer qualification;

    public UserRoleModuleESOmodel () {
    }

    public boolean isGroupLeader () {
        return groupLeader;
    }

    public void setGroupLeader (boolean groupLeader) {
        this.groupLeader = groupLeader;
    }

    public boolean isReadonly () {
        return readonly;
    }

    public void setReadonly (boolean readonly) {
        this.readonly = readonly;
    }

    public Integer getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (Integer formofstudy) {
        this.formofstudy = formofstudy;
    }

    public Long getIdHum () {
        return idHum;
    }

    public void setIdHum (Long idHum) {
        this.idHum = idHum;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getImagePath () {
        return imagePath;
    }

    public void setImagePath (String imagePath) {
        this.imagePath = imagePath;
    }

    public String getModuleName () {
        return moduleName;
    }

    public void setModuleName (String moduleName) {
        this.moduleName = moduleName;
    }

    public String getRoleName () {
        return roleName;
    }

    public void setRoleName (String roleName) {
        this.roleName = roleName;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public boolean isStudent () {
        return student;
    }

    public void setStudent (boolean student) {
        this.student = student;
    }

    public Long getIdChair () {
        return idChair;
    }

    public void setIdChair (Long idChair) {
        this.idChair = idChair;
    }

    public Long getIdDepartment () {
        return idDepartment;
    }

    public void setIdDepartment (Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    public Long getIdInstitute () {
        return idInstitute;
    }

    public void setIdInstitute (Long idInstitute) {
        this.idInstitute = idInstitute;
    }

    public Integer getFormofstudystudent () {
        return formofstudystudent;
    }

    public void setFormofstudystudent (Integer formofstudystudent) {
        this.formofstudystudent = formofstudystudent;
    }

    public Long getIdParent () {
        return idParent;
    }

    public void setIdParent (Long idParent) {
        this.idParent = idParent;
    }

    public String getFulltitle () {
        return fulltitle;
    }

    public void setFulltitle (String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getInstitute () {
        return institute;
    }

    public void setInstitute (String institute) {
        this.institute = institute;
    }

    public Long getIdInstituteMine () {
        return idInstituteMine;
    }

    public void setIdInstituteMine (Long idInstituteMine) {
        this.idInstituteMine = idInstituteMine;
    }

    public String getShorttitle () {
        return shorttitle;
    }

    public void setShorttitle (String shorttitle) {
        this.shorttitle = shorttitle;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

    public String getStartPage () {
        return startPage;
    }

    public void setStartPage (String startPage) {
        this.startPage = startPage;
    }

    public boolean isTeacher () {
        return teacher;
    }

    public void setTeacher (boolean teacher) {
        this.teacher = teacher;
    }

    public Long getIdModule () {
        return idModule;
    }

    public void setIdModule (Long idModule) {
        this.idModule = idModule;
    }

    public boolean isParent () {
        return parent;
    }

    public void setParent (boolean parent) {
        this.parent = parent;
    }

    public Integer getQualification () {
        return qualification;
    }

    public void setQualification (Integer qualification) {
        this.qualification = qualification;
    }
}
