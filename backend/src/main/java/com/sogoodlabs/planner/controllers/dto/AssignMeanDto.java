package com.sogoodlabs.planner.controllers.dto;

import java.util.List;

public class AssignMeanDto {

    private List<AssignLayerDto> layers;
    private String startDayId;
    private int tasksPerWeek;

    public List<AssignLayerDto> getLayers() {
        return layers;
    }

    public void setLayers(List<AssignLayerDto> layers) {
        this.layers = layers;
    }

    public String getStartDayId() {
        return startDayId;
    }

    public void setStartDayId(String startDayId) {
        this.startDayId = startDayId;
    }

    public int getTasksPerWeek() {
        return tasksPerWeek;
    }

    public void setTasksPerWeek(int tasksPerWeek) {
        this.tasksPerWeek = tasksPerWeek;
    }
}
