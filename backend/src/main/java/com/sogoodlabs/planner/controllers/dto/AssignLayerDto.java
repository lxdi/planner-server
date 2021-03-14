package com.sogoodlabs.planner.controllers.dto;

import java.util.ArrayList;
import java.util.List;

public class AssignLayerDto {

    private String layerId;
    private List<String> taskIds = new ArrayList<>();

    public String getLayerId() {
        return layerId;
    }

    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }
}
