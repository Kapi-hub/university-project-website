package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UltraEmailBean {
    String adminKey;
    String emailToken;
    String mailClientId;
    String mailClientSecret;

    public UltraEmailBean() {

    }

    public UltraEmailBean(String adminKey, String emailToken) {
        this.adminKey = adminKey;
        this.emailToken = emailToken;
    }

    public UltraEmailBean(String adminKey, String mailClientId, String mailClientSecret) {
        this.adminKey = adminKey;
        this.mailClientId = mailClientId;
        this.mailClientSecret = mailClientSecret;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    public String getMailClientId() {
        return mailClientId;
    }

    public void setMailClientId(String mailClientId) {
        this.mailClientId = mailClientId;
    }

    public String getMailClientSecret() {
        return mailClientSecret;
    }

    public void setMailClientSecret(String mailClientSecret) {
        this.mailClientSecret = mailClientSecret;
    }
}
