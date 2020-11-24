package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class Layer {

    @Id
    private String id;

    int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    Mean mean;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Mean getMean() {
        return mean;
    }
    public void setMean(Mean mean) {
        this.mean = mean;
    }

}
