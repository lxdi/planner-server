package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class Topic {

    @Id
    String id;

    String title;
    String source;

    @ManyToOne(fetch = FetchType.LAZY)
    Task task;

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

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }
}
