package com.sogoodlabs.planner.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LayerOrder {

    @Id
    private String id;

    @ManyToOne
    private Layer layer;
    private int layerOrder;


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

    public int getLayerOrder() {
        return layerOrder;
    }

    public void setLayerOrder(int layerOrder) {
        this.layerOrder = layerOrder;
    }
}
