package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class HQuarter implements Comparable<HQuarter>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Week startWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    Week endWeek;

    boolean custom = false;

    public HQuarter(){}

    public HQuarter(Week startWeek){
        this.startWeek = startWeek;
    }

    public HQuarter(Week startWeek, Week endWeek){
        this.startWeek = startWeek;
        this.endWeek = endWeek;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public boolean isCustom() {
        return custom;
    }
    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public Week getStartWeek() {
        return startWeek;
    }
    public void setStartWeek(Week startWeek) {
        this.startWeek = startWeek;
    }

    public Week getEndWeek() {
        return endWeek;
    }
    public void setEndWeek(Week endWeek) {
        this.endWeek = endWeek;
    }

    @Override
    public int compareTo(HQuarter hQuarter) {
        return this.getStartWeek().getStartDay().compareTo(hQuarter.getStartWeek().getStartDay());
    }
}
