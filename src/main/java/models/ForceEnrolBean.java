package models;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * A class representing a crew member and an event that they are being forced to enrol in
 * It contains a crew member and an event bean
 */
@XmlRootElement
public class ForceEnrolBean {

    private CrewMemberBean crewMember;
    private EventBean event;

    public ForceEnrolBean() {
    }

    public ForceEnrolBean(CrewMemberBean crewMember, EventBean event) {
        this.crewMember = crewMember;
        this.event = event;
    }

    public CrewMemberBean getCrewMember() {
        return crewMember;
    }

    public void setCrewMember(CrewMemberBean crewMember) {
        this.crewMember = crewMember;
    }

    public EventBean getEvent() {
        return event;
    }

    public void setEvent(EventBean event) {
        this.event = event;
    }
}
