package models;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * A class that represents the amount of each crew role required for a specific event
 * It is annotated with @XmlRootElement to allow it to be converted to and from JSON
 */
@XmlRootElement
public class EventRequiresBean {
    int eventId;
    RoleType[] roles;
    int[] amounts;

    public EventRequiresBean() {
    }

    public EventRequiresBean(int eventId, RoleType[] roles, int[] amounts) {
        this.eventId = eventId;
        this.roles = roles;
        this.amounts = amounts;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public RoleType[] getRoles() {
        return roles;
    }

    public void setRoles(RoleType[] roles) {
        this.roles = roles;
    }

    public int[] getAmounts() {
        return amounts;
    }

    public void setAmounts(int[] amounts) {
        this.amounts = amounts;
    }
}
