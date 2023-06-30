package models;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Timestamp;

/**
 * A class that represents an announcement
 * It is annotated with @XmlRootElement to allow it to be converted to and from JSON
 */
@XmlRootElement(name = "announcement")
public class AnnouncementBean {

    private int announcementID;
    private int announcer;
    private String title;
    private String body;
    private Timestamp dateTime;
    private int recipient;

    public AnnouncementBean(int announcementID, int announcer, String title, String body, boolean urgent) {
        this.announcementID = announcementID;
        this.announcer = announcer;
        this.title = title;
        this.body = body;
        this.urgent = urgent;
    }

    public AnnouncementBean(int announcementID, int announcer, String title, String body, int recipient, boolean urgent) {
        this.announcementID = announcementID;
        this.announcer = announcer;
        this.title = title;
        this.body = body;
        this.recipient = recipient;
        this.urgent = urgent;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    private boolean urgent;

    public AnnouncementBean() {}
    
    public AnnouncementBean(String title, String body, int recipient) {
        this.recipient =recipient;
        this.title = title;
        this.body = body;
    }
    public AnnouncementBean(String title, String body ) {
        this.title = title;
        this.body = body;
    }

    public AnnouncementBean(int announcementID, int announcer, String title, String body, Timestamp dateTime) {
        this.announcementID = announcementID;
        this.announcer = announcer;
        this.title = title;
        this.body = body;
        this.dateTime = dateTime;
    }

    public AnnouncementBean(int announcer, String title, String body) {
        this.announcer = announcer;
        this.title = title;
        this.body = body;
    }

    public int getAnnouncementID() {
        return announcementID;
    }

    public void setAnnouncementID(int announcementID) {
        this.announcementID = announcementID;
    }

    

    public int getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(int announcer) {
        this.announcer = announcer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }


    public int getRecipient() {
        return this.recipient;
    }
    public void setRecipient(int recipient){
        this.recipient= recipient;
    }
}


