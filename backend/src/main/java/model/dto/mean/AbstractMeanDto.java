package model.dto.mean;

import model.dto.layer.LayerDtoLazy;

import java.util.List;

/**
 * Created by Alexander on 06.03.2018.
 */
public abstract class AbstractMeanDto {

    long id;
    String title;
    String criteria;

    List<LayerDtoLazy> layers;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public List<LayerDtoLazy> getLayers() {
        return layers;
    }
    public void setLayers(List<LayerDtoLazy> layers) {
        this.layers = layers;
    }
}
