package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UltraDbBean {
    String adminKey;
    String dbPassword;
    Boolean previderBool;

    public UltraDbBean() {

    }

    public UltraDbBean(String adminKey, String dbPassword, Boolean previderBool) {
        this.adminKey = adminKey;
        this.dbPassword = dbPassword;
        this.previderBool = previderBool;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public Boolean getPreviderBool() {
        return previderBool;
    }

    public void setPreviderBool(Boolean previderBool) {
        this.previderBool = previderBool;
    }
}
