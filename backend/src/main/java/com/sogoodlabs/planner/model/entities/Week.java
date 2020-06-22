package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Day startDay;

    @ManyToOne(fetch = FetchType.LAZY)
    Day endDay;

    int number;

    public Week(){}

    public Week(Day startDay, Day endDay, int number){
        this.startDay = startDay;
        this.endDay = endDay;
        this.number = number;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Day getStartDay() {
        return startDay;
    }
    public void setStartDay(Day startDay) {
        this.startDay = startDay;
    }

    public Day getEndDay() {
        return endDay;
    }
    public void setEndDay(Day endDay) {
        this.endDay = endDay;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
