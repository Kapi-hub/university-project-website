package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CrewMemberBean extends AccountBean {
    private RoleType role;
    private Team team;

    public CrewMemberBean() {
    }

    public CrewMemberBean(int id) {
        super(id);
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
