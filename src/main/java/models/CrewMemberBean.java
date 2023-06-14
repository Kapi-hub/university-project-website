package models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Crew member")
public class CrewMemberBean extends AccountBean {
    private int id;
    private Role role;
    private Team team;

    public CrewMemberBean(String forename, String surname, String username, String emailAddress, AccountType accountType, int id, Role role, Team team) {
        super(forename, surname, username, emailAddress, accountType);
        this.id = id;
        this.role = role;
        this.team = team;
    }


    public CrewMemberBean(String username, String password, int id, Role role, Team team) {
        super(username, password);
        this.id = id;
        this.role = role;
        this.team = team;
    }

    public CrewMemberBean(int id, Role role, Team team) {
        this.id = id;
        this.role = role;
        this.team = team;
    }

    public CrewMemberBean(int accountId, String forename, String surname, String username, String emailAddress, String password, AccountType accountType, int id, Role role, Team team) {
        super(accountId, forename, surname, username, emailAddress, password, accountType);
        this.id = id;
        this.role = role;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }


}
