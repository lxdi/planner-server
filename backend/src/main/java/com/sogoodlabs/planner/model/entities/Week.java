package com.sogoodlabs.planner.model.entities;


import com.sogoodlabs.common_mapper.annotations.IncludeInDto;

import jakarta.persistence.*;
import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
@Table(name = "weeks")
public class Week {

    @Id
    private String id;

    @Column(name = "year_of_week")
    private int year;
    private int num;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev")
    private Week prev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next")
    private Week next;

    @Transient
    private List<Day> days;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }

    public Week getPrev() {
        return prev;
    }

    public void setPrev(Week prev) {
        this.prev = prev;
    }

    public Week getNext() {
        return next;
    }

    public void setNext(Week next) {
        this.next = next;
    }

    @IncludeInDto
    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
