package resources;

import dao.AdminDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import models.*;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@Path("/admin")
public class AdminResource {

    /* METHODS RELATED TO ANNOUNCEMENTS */
    @POST
    @Path("/dashboard/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void handlePostAnnouncement(AnnouncementBean announcement, @CookieParam("accountId") String accountIdString) {
        try {
            int accountId = Integer.parseInt(accountIdString);
            announcement.setAnnouncer(accountId);
            AdminDao.I.addAnnouncement(announcement);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/announcements")
    @RolesAllowed("admin")
    public String getAnnouncement() {
        try {
            return AdminDao.I.getAllAnnouncements();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }


    /* METHODS RELATED TO EVENTS */
//    @POST
//    @Path("/crewAssignments")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed("admin")
//    public void handleCreateNewEvent(FormBean form) throws SQLException {
//        try {
//            int client_id = AdminDao.I.addClient(form.getClientBean());
//            form.getEventBean().setClient_id(client_id);
//            int event_id = AdminDao.I.addEvent(form.getEventBean());
//            for (RequiredCrewBean required : form.getRequiredCrewBeans()) {
//                required.setEvent_id(event_id);
//                AdminDao.I.addRequirement(required);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @GET
    @Path("/events")
    @RolesAllowed("admin")
    public String getLatestEvent() {
        try {
            return AdminDao.I.getLatestEvent();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    @GET
    @Path("/crewReq")
    @RolesAllowed("admin")
    public List<EventBean> showEventsWithoutCrew() {
        List<EventBean> events = null;
        try{
            events = AdminDao.I.getNotFullEvents();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return events;
    }

    /* METHODS RELATED TO CREW-MEMBERS */
    @POST
    @Path("/crewAssignments")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void handleCreateNewMember(CrewMemberBean crewMember) {
        try {
            if (validEmail(crewMember) && validPassword(crewMember)) {
                AdminDao.I.createNewMember(crewMember);
            } else{
                //TODO handle this part if the crewmember doesn't have the valid credentials
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * user stories
     * 33 The password must contain at least 8 characters.
     * 34 The password must contain capital letters, symbols and at least a digit.
     *  -> for better security, changed to at least one symbol, one digit, one capital letter, and one small letter + no spacebars.
     * @param crewMember
     * @return
     */
    public static boolean validPassword(CrewMemberBean crewMember) {
        boolean hasDigit = false;
        boolean hasSymbol = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpacebar = false;
        char[] passwordChar = crewMember.getPassword().toCharArray();
        for (char c : passwordChar) {
            if (Character.isDigit(c)) {
                hasDigit = true;
                continue;
            }
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
                continue;
            }
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
                continue;
            }
            if (Character.isSpaceChar(c)){
                hasSpacebar = true;
                continue;
            }
                hasSymbol = true;

        }
        return crewMember.getPassword().length() >= 8 && !hasSpacebar && hasDigit && hasSymbol && hasUpperCase && hasLowerCase;
    }

    /**
     * Only checks for valid syntax of email according to official guidelines of valid emails.
     * It does not check if the email actually exists as well.
     * @param crewMember
     * @return
     */
    public static boolean validEmail(CrewMemberBean crewMember) {
        if (Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
                .matcher(crewMember.getEmailAddress())
                .matches()){
                    return true;
        }
        return false;
    }
    @GET
    @Path("/crewAssignments")
    @RolesAllowed("admin")
    public String getAllCrewMembers() {
        try {
            return AdminDao.I.getAllCrewMembers();
        }  catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/crewAssignments")
    @RolesAllowed("admin")
    public String getProducers() {
        try {
            return AdminDao.I.getProducers();
        }  catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
