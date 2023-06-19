package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UltraAdminBean {
    String adminKey;

    public UltraAdminBean() {

    }

    public UltraAdminBean(String adminKey) {
        this.adminKey = adminKey;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }
}
