package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FormBean {
    private EventBean eventBean;
    private ClientBean clientBean;

    public EventBean getEventBean() {
        return eventBean;
    }

    public void setEventBean(EventBean eventBean) {
        this.eventBean = eventBean;
    }

    public ClientBean getClientBean() {
        return clientBean;
    }

    public void setClientBean(ClientBean clientBean) {
        this.clientBean = clientBean;
    }


}
