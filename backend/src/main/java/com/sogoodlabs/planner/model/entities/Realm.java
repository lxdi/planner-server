package com.sogoodlabs.planner.model.entities;


import javax.persistence.*;

@Entity
public class Realm {

    @Id
    String id;
    String title;

    boolean current = false;

    public String getId() {
        return id;
    }
    public void setId(String id) {
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
