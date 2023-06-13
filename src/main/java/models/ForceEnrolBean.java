package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ForceEnrolBean {

    private CrewMemberBean crewMemberBean;
    private EventBean eventBean;

    public ForceEnrolBean() {
    }

    public CrewMemberBean getCrewMemberBean() {
        return crewMemberBean;
    }

    public void setCrewMemberBean(CrewMemberBean crewMemberBean) {
        this.crewMemberBean = crewMemberBean;
    }

    public EventBean getEventBean() {
        return eventBean;
    }

    public void setEventBean(EventBean eventBean) {
        this.eventBean = eventBean;
    }
}
