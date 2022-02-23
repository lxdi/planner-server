package com.sogoodlabs.planner.model.entities;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;

import javax.persistence.*;

@Entity
public class TaskMapper {

    @Id
    String id;

    @OneToOne(fetch = FetchType.LAZY)
    Task task;

    @OneToOne(fetch = FetchType.LAZY)
    Day planDay;

    @OneToOne(fetch = FetchType.LAZY)
    Day finishDay;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }

    @IncludeInDto
    public Day getPlanDay() {
        return planDay;
    }
    public void setPlanDay(Day planDay) {
        this.planDay = planDay;
    }

    @IncludeInDto
    public Day getFinishDay() {
        return finishDay;
    }
    public void setFinishDay(Day finishDay) {
        this.finishDay = finishDay;
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
