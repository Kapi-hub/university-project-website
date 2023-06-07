package models;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@XmlRootElement
public class EventBean {
    private int id;
    private int client_id;
    private String name;
    private String description;
    private Timestamp start;
    private int duration;
    private String location;
    private int production_manager_id;
    private EventType type;
    private EventStatus status;

    public EventBean() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
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

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

}