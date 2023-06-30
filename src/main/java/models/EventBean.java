package models;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Timestamp;

/**
 * An event bean following the database structure.
 */
@XmlRootElement(name = "event")
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
    private BookingType booking_type;
    private EventStatus status;

    public EventBean(String name, String description, Timestamp start, int duration, String location, EventType type, BookingType booking_type) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.duration = duration;
        this.location = location;
        this.type = type;
        this.booking_type = booking_type;
    }

    public EventBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public EventBean() {

    }

    public EventBean(int id, int client_id, String name, String description, Timestamp start, int duration, String location, int production_manager_id, EventType type, BookingType booking_type, EventStatus status) {
        this.id = id;
        this.client_id = client_id;
        this.name = name;
        this.description = description;
        this.start = start;
        this.duration = duration;
        this.location = location;
        this.production_manager_id = production_manager_id;
        this.type = type;
        this.booking_type = booking_type;
        this.status = status;
    }

    public EventBean(int eventId) {
        this.id= eventId;
    }

    public BookingType getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(BookingType booking_type) {
        this.booking_type = booking_type;
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