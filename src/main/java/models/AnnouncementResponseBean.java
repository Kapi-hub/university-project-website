package models;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Timestamp;

/**
 * A class that represents an announcement
 * It is annotated with @XmlRootElement to allow it to be converted to and from JSON
 */
@XmlRootElement
public class AnnouncementResponseBean {
    private int id;
    private String announcer;
    private String title;
    private String body;
    private Timestamp dateTime;
    private boolean isPersonal;
    private boolean urgent;

    public AnnouncementResponseBean() {
    }

    public boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public boolean getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(boolean isPersonal) {
        this.isPersonal = isPersonal;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(String announcer) {
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
}
