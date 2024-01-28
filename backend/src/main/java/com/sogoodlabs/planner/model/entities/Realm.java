package com.sogoodlabs.planner.model.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "realms")
public class Realm {

    @Id
    String id;
    int priority;
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

    public boolean getCurrent() {
        return current;
    }
    public void setCurrent(boolean current) {
        this.current = current;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
