package models;

public class RequiredCrewBean {
    private int event_id;
    private RoleType role;
    private int crew_size;

    public RequiredCrewBean() {
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public int getCrew_size() {
        return crew_size;
    }

    public void setCrew_size(int crew_size) {
        this.crew_size = crew_size;
    }


}
