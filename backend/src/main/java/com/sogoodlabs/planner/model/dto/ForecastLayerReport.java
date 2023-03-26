package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.planner.model.entities.Layer;

import java.sql.Date;

public class ForecastLayerReport {

    private Layer layer;
    private Date finishAllTasksDate;
    private Date finish70percentReps;


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

    public Date getFinish70percentReps() {
        return finish70percentReps;
    }

    public void setFinish70percentReps(Date finish70percentReps) {
        this.finish70percentReps = finish70percentReps;
    }
}
