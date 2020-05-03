package com.sogoodlabs.planner.model.entities;


import javax.persistence.*;

@Entity
public class Realm {

    public Realm(){}

    public Realm(String title){
        this.setTitle(title);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;

    boolean current = false;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCurrent() {
        return current;
    }
    public void setCurrent(boolean current) {
        this.current = current;
    }
}
