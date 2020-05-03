package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String title;
    String source;

    @ManyToOne(fetch = FetchType.LAZY)
    Task task;

    public Topic(){}

    public Topic(String title, Task task){
//        if(title==null || title.isEmpty() || task==null){
//            throw new RuntimeException("Topic to create is not full");
//        }
        this.title = title;
        this.task = task;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
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
