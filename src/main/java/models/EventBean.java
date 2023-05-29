package models;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;
import java.sql.Time;

@XmlRootElement
public class EventBean {
    private int id;
    private String name;
    private EventType type;
    private Date date;
    private Time start_time;
    private int duration;
    private String location;
    private int production_manager_id;
    private int crew_members;
    private int client_id;
    private EventStatus status;

    public EventBean(int id, String name, EventType type, Date date, Time start_time, int duration, String location, int production_manager_id, int crew_members, int client_id, EventStatus status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.start_time = start_time;
        this.duration = duration;
        this.location = location;
        this.production_manager_id = production_manager_id;
        this.crew_members = crew_members;
        this.client_id = client_id;
        this.status = status;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Time getStart_time() {
        return start_time;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getProduction_manager_id() {
        return production_manager_id;
    }

    public void setProduction_manager_id(int production_manager_id) {
        this.production_manager_id = production_manager_id;
    }

    public int getCrew_members() {
        return crew_members;
    }

    public void setCrew_members(int crew_members) {
        this.crew_members = crew_members;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }


}
