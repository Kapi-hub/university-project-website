package models;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * A class with the information necessary for the client.
 * It extends the client with just a phone number.
 */
@XmlRootElement
public class ClientBean extends AccountBean {
    private String phone_number;

    public ClientBean() {

    }

    public ClientBean(String forename, String lastname, String username, String email_address, String phone_number) {
        super(forename, lastname, username, email_address, AccountType.CLIENT);
        this.phone_number = phone_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
