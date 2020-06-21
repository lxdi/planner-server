package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class Repetition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    Day planDay;

    @ManyToOne(fetch = FetchType.LAZY)
    Day factDay;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Day getPlanDay() {
        return planDay;
    }
    public void setPlanDay(Day planDay) {
        this.planDay = planDay;
    }

    public Day getFactDay() {
        return factDay;
    }
    public void setFactDay(Day factDay) {
        this.factDay = factDay;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
