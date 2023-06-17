package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "crew_member")
public class CrewMemberBean extends AccountBean {
    private RoleType role;
    private Team team;

    public CrewMemberBean() {
    }

    public CrewMemberBean(int id, String forename, String surname) {
        super(id, forename, surname);
    }

    public CrewMemberBean(RoleType role, Team team) {
        this.role = role;
        this.team = team;
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
