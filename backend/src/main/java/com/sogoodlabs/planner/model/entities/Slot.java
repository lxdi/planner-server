package com.sogoodlabs.planner.model.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "slots")
public class Slot {

    @Id
    String id;

    private int hours;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DaysOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "realm", nullable = false)
    private Realm realm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public DaysOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DaysOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }
}
