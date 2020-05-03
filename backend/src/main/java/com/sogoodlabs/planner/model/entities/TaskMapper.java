package com.sogoodlabs.planner.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class TaskMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne(fetch = FetchType.LAZY)
    Task task;

    @OneToOne(fetch = FetchType.LAZY)
    SlotPosition slotPosition;

    @OneToOne(fetch = FetchType.LAZY)
    Week week;

    @Column(name = "finishdate")
    @Basic
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date finishDate;

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

    public SlotPosition getSlotPosition() {
        return slotPosition;
    }

    public void setSlotPosition(SlotPosition slotPosition) {
        this.slotPosition = slotPosition;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public Date getFinishDate() {
        return finishDate;
    }
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
