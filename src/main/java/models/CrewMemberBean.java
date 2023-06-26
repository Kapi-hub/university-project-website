package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="crew_member")
public class CrewMemberBean extends AccountBean {
    private int id;
    private RoleType role;
    private Team team;

    public CrewMemberBean() {

    }

    public CrewMemberBean(String forename, String surname, String username, String emailAddress, AccountType accountType, int id, RoleType role, Team team) {
        super(forename, surname, username, emailAddress, accountType);
        this.id = id;
        this.role = role;
        this.team = team;
    }


    public CrewMemberBean(String username, String password, int id, RoleType role, Team team) {
        super(username, password);
        this.id = id;
        this.role = role;
        this.team = team;
    }

    public CrewMemberBean(int id, RoleType role, Team team) {
        this.id = id;
        this.role = role;
        this.team = team;
    }

    public CrewMemberBean(int accountId, String forename, String surname, String username, String emailAddress, String password, AccountType accountType, int id, RoleType role, Team team) {
        super(accountId, forename, surname, username, emailAddress, password, accountType);
        this.id = id;
        this.role = role;
        this.team = team;
    }

    public CrewMemberBean(String forename, String surname, String username, String email, RoleType role, Team team) {
        super(forename, surname, username, email, AccountType.CREW_MEMBER);
        this.role = role;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
