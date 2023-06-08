package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "account")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountBean {
    private int accountId;
    private String forename;
    private String surname;

    public AccountBean(String forename, String surname, String username, String email_address, AccountType accountType) {
        this.forename = forename;
        this.surname = surname;
        this.username = username;
        this.email_address = email_address;
        this.accountType = accountType;
    }

    @JsonProperty("username")
    private String username;
    private String email_address;
    @JsonProperty("password")
    private String password;
    private String sessionId;
    private AccountType accountType;

    public AccountBean(String username, String password) {
        this.username = username;
        this.password = password;
    }



    public AccountBean() {
    }

    public AccountBean(int accountId, String sessionId) {
        this.accountId = accountId;
        this.sessionId = sessionId;
    }



    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
