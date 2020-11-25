package com.sogoodlabs.planner.model.entities;


import javax.persistence.*;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Week {

    @Id
    private String id;

    private int year;
    private int num;

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
}
