package com.sogoodlabs.planner.model.dto;

import java.util.ArrayList;
import java.util.List;

public class AssignMeanDto {

    private List<AssignLayerDto> layers = new ArrayList<>();
    private String startDayId;

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

}
