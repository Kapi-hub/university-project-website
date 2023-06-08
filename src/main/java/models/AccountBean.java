package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class AccountBean {
    private int accountId;
    private String forename;
    private String surname;
    private String username;
    private String emailAddress;
    private String password;
    private AccountType accountType;

    public AccountBean(String forename, String surname, String username, String emailAddress, AccountType accountType) {
        this.forename = forename;
        this.surname = surname;
        this.username = username;
        this.emailAddress = emailAddress;
        this.accountType = accountType;
    }

    public AccountBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AccountBean() {
    }

    public AccountBean(int accountId, String forename, String surname, String username, String emailAddress, String password, AccountType accountType) {
        this.accountId = accountId;
        this.forename = forename;
        this.surname = surname;
        this.username = username;
        this.emailAddress = emailAddress;
        this.password = password;
        this.accountType = accountType;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
