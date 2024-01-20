package com.sogoodlabs.planner.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "task_testings")
public class TaskTesting {

    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task")
    Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private TaskTesting parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next")
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
