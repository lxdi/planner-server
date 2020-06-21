package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class TaskMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne(fetch = FetchType.LAZY)
    Task task;

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

    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
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
}
