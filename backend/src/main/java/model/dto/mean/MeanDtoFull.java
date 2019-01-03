package model.dto.mean;

import model.dto.layer.LayerDtoLazy;

import java.util.List;

public class MeanDtoFull extends MeanDtoLazy {

    String criteria;
    List<LayerDtoLazy> layers;

    public List<LayerDtoLazy> getLayers() {
        return layers;
    }
    public void setLayers(List<LayerDtoLazy> layers) {
        this.layers = layers;
    }

    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

}
