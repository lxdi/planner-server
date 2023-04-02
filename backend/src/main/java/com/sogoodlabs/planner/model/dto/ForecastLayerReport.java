package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.planner.model.entities.Layer;

import java.sql.Date;

public class ForecastLayerReport {

    private String id;

    private Layer layer;
    private Date finishAllTasksDate;
    private Date mostRepsDoneDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public Date getFinishAllTasksDate() {
        return finishAllTasksDate;
    }

    public void setFinishAllTasksDate(Date finishAllTasksDate) {
        this.finishAllTasksDate = finishAllTasksDate;
    }

    public Date getMostRepsDoneDate() {
        return mostRepsDoneDate;
    }

    public void setMostRepsDoneDate(Date mostRepsDoneDate) {
        this.mostRepsDoneDate = mostRepsDoneDate;
    }
}
