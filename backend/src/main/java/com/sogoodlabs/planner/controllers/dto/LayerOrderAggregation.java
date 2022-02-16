package com.sogoodlabs.planner.controllers.dto;

import com.sogoodlabs.planner.model.entities.LayerOrder;
import com.sogoodlabs.planner.model.entities.Mean;

import java.util.List;

public class LayerOrderAggregation {

    private List<LayerOrder> layerOrderList;
    private List<Mean> means;

    public List<LayerOrder> getLayerOrderList() {
        return layerOrderList;
    }

    public void setLayerOrderList(List<LayerOrder> layerOrderList) {
        this.layerOrderList = layerOrderList;
    }

    public List<Mean> getMeans() {
        return means;
    }

    public void setMeans(List<Mean> means) {
        this.means = means;
    }
}
