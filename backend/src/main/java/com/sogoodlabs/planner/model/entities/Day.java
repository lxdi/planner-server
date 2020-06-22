package com.sogoodlabs.planner.model.entities;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Day implements Comparable<Day>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    @Basic
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date date;

    @Column(name = "dayofweek")
    @Enumerated(EnumType.STRING)
    DaysOfWeek dayOfWeek;


    public Day(){}

    public Day(Date date){
        this.date = date;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public DaysOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DaysOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public int compareTo(Day o) {
        return this.date.compareTo(o.getDate());
    }
}
