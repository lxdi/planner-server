package com.sogoodlabs.planner.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sogoodlabs.common_mapper.annotations.IncludeInDto;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Repetition {

    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    Task task;

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

    @IncludeInDto
    public Day getPlanDay() {
        return planDay;
    }
    public void setPlanDay(Day planDay) {
        this.planDay = planDay;
    }

    @IncludeInDto
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

    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }

    @IncludeInDto
    public boolean getIsMemo(){
        if(this.getTask().getRepetitionPlan()==null){
            return false;
        }

        return this.getTask().getRepetitionPlan().getDayStep();
    }

    public Mean getMean(){
        if(this.task==null){
            return null;
        }
        if(this.task.getLayer()==null){
            return null;
        }
        return task.getLayer().getMean();
    }

    @IncludeInDto
    public String getTaskFullPath(){
        return getTask().getFullPath();
    }
}
