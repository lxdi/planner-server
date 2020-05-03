package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Task{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;


    @ManyToOne(fetch = FetchType.LAZY)
    Subject subject;
    int position;

    //@Cascade(org.hibernate.annotations.CascadeType.ALL)
//    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    Set<Topic> topics = new HashSet<>();
//
//    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    Set<TaskTesting> testings = new HashSet<>();

    public Task(){}

    public Task(String title, Subject subject, int position){
        assert subject!=null && position>0 && title!=null && !title.isEmpty();
        this.title = title;
        this.subject = subject;
        this.position = position;
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

    public Subject getSubject() {
        return subject;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

}
