package com.sogoodlabs.planner.model.entities;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Day {

    @Id
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    private Week week;

    @Enumerated(EnumType.STRING)
    private DaysOfWeek weekDay;

    @Basic
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    private int num;

    @Transient
    private int mappersNum;

    @Transient
    private int mappersNumUnfinished;

    @Transient
    private int repsNum;

    @Transient
    private int repsNumUnfinished;

    @Transient
    private int externalTasksNum;

    @Transient
    private String urgency;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public DaysOfWeek getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(DaysOfWeek weekDay) {
        this.weekDay = weekDay;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getMappersNum() {
        return mappersNum;
    }

    public void setMappersNum(int mappersNum) {
        this.mappersNum = mappersNum;
    }

    public int getRepsNum() {
        return repsNum;
    }

    public void setRepsNum(int repsNum) {
        this.repsNum = repsNum;
    }

    public int getMappersNumUnfinished() {
        return mappersNumUnfinished;
    }

    public void setMappersNumUnfinished(int mappersNumUnfinished) {
        this.mappersNumUnfinished = mappersNumUnfinished;
    }

    public int getRepsNumUnfinished() {
        return repsNumUnfinished;
    }

    public void setRepsNumUnfinished(int repsNumUnfinished) {
        this.repsNumUnfinished = repsNumUnfinished;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public int getExternalTasksNum() {
        return externalTasksNum;
    }

    public void setExternalTasksNum(int externalTasksNum) {
        this.externalTasksNum = externalTasksNum;
    }
}
