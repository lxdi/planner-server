package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;
    int position;

    @ManyToOne(fetch = FetchType.LAZY)
    Layer layer;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    public Subject(){}

    public Subject(Layer layer, int position){
        assert layer!=null && position>0;
        this.layer = layer;
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

    public Layer getLayer() {
        return layer;
    }
    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public List<Task> getTasks() {
        return tasks;
    }
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
