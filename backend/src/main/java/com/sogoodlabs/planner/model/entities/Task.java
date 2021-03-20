package com.sogoodlabs.planner.model.entities;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.common_mapper.annotations.MapToClass;
import com.sogoodlabs.planner.model.IEntity;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Task implements IEntity {

    @Id
    String id;
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    Layer layer;

    int position;

    @Transient
    private List<Topic> topics;

    @Transient
    private List<TaskTesting> taskTestings;

    @Transient
    private String progressStatus;

    @Override
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

    @IncludeInDto
    public List<Topic> getTopics() {
        return topics;
    }

    @MapToClass(value = Topic.class)
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @IncludeInDto
    public List<TaskTesting> getTaskTestings() {
        return taskTestings;
    }

    @MapToClass(value = TaskTesting.class)
    public void setTaskTestings(List<TaskTesting> taskTestings) {
        this.taskTestings = taskTestings;
    }

    public String getProgressStatus() {
        return progressStatus;
    }
    public void setProgressStatus(String progressStatus) {
        this.progressStatus = progressStatus;
    }

    @IncludeInDto
    public String getFullPath(){

        List<String> result = new ArrayList<>();
        try {
            result.add(this.title);
            result.add(""+this.layer.getPriority());
            result.add(this.layer.getMean().getTitle());
            result.add(this.layer.getMean().getRealm().getTitle());
        } catch (NullPointerException npe){}

        Collections.reverse(result);
        return String.join("/", result);
    }
}
