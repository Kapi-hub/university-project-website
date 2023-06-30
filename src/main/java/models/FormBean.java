package models;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * A class that represents the information required to create an event
 * It contains an event bean, a client bean and an array of required crew beans
 * It is annotated with @XmlRootElement to allow it to be converted to and from JSON
 */
@XmlRootElement
public class FormBean {
    private EventBean eventBean;
    private ClientBean clientBean;
    private RequiredCrewBean[] requiredCrewBeans;

    public RequiredCrewBean[] getRequiredCrewBeans() {
        return requiredCrewBeans;
    }

    public void setRequiredCrewBeans(RequiredCrewBean[] requiredCrewBeans) {
        this.requiredCrewBeans = requiredCrewBeans;
    }

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
