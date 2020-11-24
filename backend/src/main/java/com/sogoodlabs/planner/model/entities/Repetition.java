package com.sogoodlabs.planner.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Repetition {

    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    RepetitionPlan repetitionPlan;

    @OneToOne(fetch = FetchType.LAZY)
    Day planDay;

    @OneToOne(fetch = FetchType.LAZY)
    Day factDay;

    private boolean isRepetitionOnly = false;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public RepetitionPlan getRepetitionPlan() {
        return repetitionPlan;
    }
    public void setRepetitionPlan(RepetitionPlan repetitionPlan) {
        this.repetitionPlan = repetitionPlan;
    }

    public Day getPlanDay() {
        return planDay;
    }
    public void setPlanDay(Day planDay) {
        this.planDay = planDay;
    }

    public Day getFactDay() {
        return factDay;
    }
    public void setFactDay(Day factDay) {
        this.factDay = factDay;
    }

    public boolean getIsRepetitionOnly() {
        return isRepetitionOnly;
    }
    public void setIsRepetitionOnly(boolean repetitionOnly) {
        isRepetitionOnly = repetitionOnly;
    }
}
