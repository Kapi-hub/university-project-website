package models;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Timestamp;
import java.util.Arrays;

@XmlRootElement
public class EventResponseBean {
    int id;
    String name;
    EventType type;
    Timestamp date;
    String location;
    int duration;
    String client;
    BookingType bookingType;
    String productionManager;
    Object[] crew;
    Object[] enrolled;
    EventStatus status;
    String description;
    boolean isEnrolled;
    boolean canEnrol;

    public EventResponseBean() {
    }

    public EventResponseBean(int id, String name, EventType type, Timestamp date, String location, int duration, String client, BookingType bookingType, String productionManager, Object[] crew, Object[] enrolled, EventStatus status, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.location = location;
        this.duration = duration;
        this.client = client;
        this.bookingType = bookingType;
        this.productionManager = productionManager;
        this.crew = crew;
        this.enrolled = enrolled;
        this.status = status;
        this.description = description;
    }

    public boolean getIsEnrolled() {
        return isEnrolled;
    }

    public void setIsEnrolled(boolean isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public boolean getCanEnrol() {
        return canEnrol;
    }

    public void setCanEnrol(boolean canEnrol) {
        this.canEnrol = canEnrol;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public String getProductionManager() {
        return productionManager;
    }

    public void setProductionManager(String productionManager) {
        this.productionManager = productionManager;
    }

    public Object[] getCrew() {
        return crew;
    }

    public void setCrew(Object[] crew) {
        this.crew = crew;
    }

    public Object[] getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Object[] enrolled) {
        this.enrolled = enrolled;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EventResponseBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", duration=" + duration +
                ", client='" + client + '\'' +
                ", bookingType=" + bookingType +
                ", productionManager='" + productionManager + '\'' +
                ", crew=" + Arrays.toString(crew) +
                ", enrolled=" + Arrays.toString(enrolled) +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", isEnrolled=" + isEnrolled +
                ", canEnrol=" + canEnrol +
                '}';
    }
}
