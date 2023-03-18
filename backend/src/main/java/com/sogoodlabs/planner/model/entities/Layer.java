package com.sogoodlabs.planner.model.entities;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.common_mapper.annotations.MapToClass;
import com.sogoodlabs.planner.model.IEntity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Layer implements IEntity {

    @Id
    private String id;

    int priority;
    int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    Mean mean;

    @Transient
    private List<Task> tasks;

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

    public int getDepth() {
        return depth;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Mean getMean() {
        return mean;
    }
    public void setMean(Mean mean) {
        this.mean = mean;
    }

    @IncludeInDto
    public List<Task> getTasks() {
        return tasks;
    }

    @MapToClass(value = Task.class, parentField = "layer")
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
