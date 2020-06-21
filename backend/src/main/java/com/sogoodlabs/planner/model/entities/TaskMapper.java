package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class TaskMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne(fetch = FetchType.LAZY)
    Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    Date day;

    @Column(name = "finishday")
    @ManyToOne(fetch = FetchType.LAZY)
    Date finishDay;

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

    public Date getDay() {
        return day;
    }
    public void setDay(Date day) {
        this.day = day;
    }

    public Date getFinishDay() {
        return finishDay;
    }
    public void setFinishDay(Date finishDay) {
        this.finishDay = finishDay;
    }
}
