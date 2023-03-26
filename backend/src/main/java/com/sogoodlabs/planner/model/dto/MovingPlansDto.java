package com.sogoodlabs.planner.model.dto;

import java.util.List;

public class MovingPlansDto {

    private String targetDayId;
    private List<String> taskMappersIds;
    private List<String> repetitionIds;

    public String getTargetDayId() {
        return targetDayId;
    }

    public void setTargetDayId(String targetDayId) {
        this.targetDayId = targetDayId;
    }

    public List<String> getTaskMappersIds() {
        return taskMappersIds;
    }

    public void setTaskMappersIds(List<String> taskMappersIds) {
        this.taskMappersIds = taskMappersIds;
    }

    public List<String> getRepetitionIds() {
        return repetitionIds;
    }

    public void setRepetitionIds(List<String> repetitionIds) {
        this.repetitionIds = repetitionIds;
    }
}
