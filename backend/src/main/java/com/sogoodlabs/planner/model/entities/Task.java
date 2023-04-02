package com.sogoodlabs.planner.model.entities;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.common_mapper.annotations.MapToClass;
import com.sogoodlabs.planner.model.dto.TaskProgressDto;
import com.sogoodlabs.planner.model.IEntity;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Task implements IEntity {

    public enum TaskStatus {
        CREATED, COMPLETED;

        public String value() {return this.name();}


    }


    @Id
    String id;
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    Layer layer;

    int position;

    @ManyToOne(fetch = FetchType.LAZY)
    private RepetitionPlan repetitionPlan;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.CREATED;


    //---------------------
    @Transient
    private List<Topic> topics;

    @Transient
    private List<TaskTesting> taskTestings;

    @Transient
    private String progressStatus;

    @Transient
    private TaskProgressDto progress;

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

    public RepetitionPlan getRepetitionPlan() {
        return repetitionPlan;
    }

    public void setRepetitionPlan(RepetitionPlan repetitionPlan) {
        this.repetitionPlan = repetitionPlan;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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

    @MapToClass(value = TaskTesting.class, parentField = "task")
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
    public TaskProgressDto getProgress() {
        return progress;
    }
    public void setProgress(TaskProgressDto progress) {
        this.progress = progress;
    }

    @IncludeInDto
    public String getFullPath(){

        List<String> result = new ArrayList<>();
        try {
            result.add(this.title);
            result.add(""+this.layer.getDepth());
            result.add(this.layer.getMean().getTitle());
            result.add(this.layer.getMean().getRealm().getTitle());
        } catch (NullPointerException npe){}

        Collections.reverse(result);
        return String.join("/", result);
    }


}
