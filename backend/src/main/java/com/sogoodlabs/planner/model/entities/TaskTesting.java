package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

@Entity
public class TaskTesting {

    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskTesting parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskTesting next;

    String question;

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

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public TaskTesting getParent() {
        return parent;
    }

    public void setParent(TaskTesting parent) {
        this.parent = parent;
    }

    public TaskTesting getNext() {
        return next;
    }

    public void setNext(TaskTesting next) {
        this.next = next;
    }
}
