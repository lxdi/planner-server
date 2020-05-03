package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class MapperExclusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    private Week week;

    @ManyToOne
    private SlotPosition slotPosition;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Week getWeek() {
        return week;
    }
    public void setWeek(Week week) {
        this.week = week;
    }

    public SlotPosition getSlotPosition() {
        return slotPosition;
    }
    public void setSlotPosition(SlotPosition slotPosition) {
        this.slotPosition = slotPosition;
    }
}
