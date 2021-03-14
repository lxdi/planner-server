package com.sogoodlabs.planner.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RepetitionPlan {

    @Id
    String id;

    String title;

    int[] plan;

    boolean dayStep;

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

    public int[] getPlan() {
        return plan;
    }
    public void setPlan(int[] plan) {
        this.plan = plan;
    }

    public boolean getDayStep() {
        return dayStep;
    }
    public void setDayStep(boolean dayStep) {
        this.dayStep = dayStep;
    }
}
