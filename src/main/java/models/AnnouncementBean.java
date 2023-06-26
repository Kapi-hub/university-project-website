package models;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Timestamp;

@XmlRootElement(name = "announcement")

public class AnnouncementBean {

    private int announcementID;
    private int announcer;
    private String title;
    private String body;
    private Timestamp dateTime;

    public AnnouncementBean() {}

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

    public AnnouncementBean(String title, String body) {
        this.title = title;
        this.body = body;
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


}


