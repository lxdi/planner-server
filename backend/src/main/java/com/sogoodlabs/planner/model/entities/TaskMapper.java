package com.sogoodlabs.planner.model.entities;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;

import jakarta.persistence.*;

@Entity
@Table(name = "task_mappers")
public class TaskMapper {

    @Id
    String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task")
    Task task;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_day")
    Day planDay;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finish_day")
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
