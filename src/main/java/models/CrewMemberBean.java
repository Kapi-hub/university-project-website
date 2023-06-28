package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class CrewMemberBean extends AccountBean {
    private RoleType role;
    private Team team;

    public CrewMemberBean(int id){
        super(id);
    }

    /**
     * creation of account CrewMember by admin
     */
    public CrewMemberBean(String forename, String surname, String username, String emailAddress, String password, RoleType role, Team team) {
        super(forename, surname, username, emailAddress, password, AccountType.CREW_MEMBER);
        this.role = role;
        this.team = team;
    }
    public CrewMemberBean() {
        super();
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
