package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SessionBean {
    private int accountId;
    private String sessionId;

    public SessionBean() {
    }

    public SessionBean(int accountId, String sessionId) {
        this.accountId = accountId;
        this.sessionId = sessionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
