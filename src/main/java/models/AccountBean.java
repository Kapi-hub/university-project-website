package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class AccountBean {
    private int id;
    private String forename;
    private String surname;
    private String username;
    private String email_address;
    private String password;
    private AccountType accountType;

    public AccountBean(String forename, String surname, String username, String email_address, AccountType accountType) {
        this.forename = forename;
        this.surname = surname;
        this.username = username;
        this.email_address = email_address;
        this.accountType = accountType;
    }

    public AccountBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AccountBean() {
    }

    public AccountBean(int id, String forename, String surname, String username, String emailAddress, String password, AccountType accountType) {
        this.id = id;
        this.forename = forename;
        this.surname = surname;
        this.username = username;
        this.emailAddress = emailAddress;
        this.password = password;
        this.accountType = accountType;
    }

    public AccountBean(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
