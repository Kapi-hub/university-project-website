package models;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * A class with the information necessary for the crew member.
 * It extends the account with a role and team.
 */
@XmlRootElement(name="crew_member")
public class CrewMemberBean extends AccountBean {
    private int id;
    private RoleType role;
    private Team team;


    public CrewMemberBean() {

    }

    public CrewMemberBean(String forename, String surname, String username, String emailAddress, AccountType accountType, RoleType role, Team team) {
        super(forename, surname, username, emailAddress, accountType);
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

    /**
     * creation of account CrewMember by admin
     */
    public CrewMemberBean(String forename, String surname, String username, String emailAddress, String password, RoleType role, Team team) {
        super(forename, surname, username, emailAddress, password, AccountType.CREW_MEMBER);
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
