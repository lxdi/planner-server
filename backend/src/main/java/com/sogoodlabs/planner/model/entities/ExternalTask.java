package com.sogoodlabs.planner.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "external_tasks")
public class ExternalTask {

    @Id
    private String id;
    private String description;
    private int hours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day")
    private Day day;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }
}
