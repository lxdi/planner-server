package com.sogoodlabs.planner.model.entities;


import javax.persistence.*;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private int year;
    private int number;

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

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
