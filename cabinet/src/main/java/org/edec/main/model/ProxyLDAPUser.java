package org.edec.main.model;

public class ProxyLDAPUser {
    private Integer id;
    private String family;
    private String name;
    private String patron;

    public ProxyLDAPUser() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFamily() {
        return this.family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatron() {
        return this.patron;
    }

    public void setPatron(String patron) {
        this.patron = patron;
    }
}
