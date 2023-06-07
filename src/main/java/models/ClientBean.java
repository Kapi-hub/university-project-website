package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClientBean extends AccountBean {
    private String phone_number;

    public ClientBean() {

    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
