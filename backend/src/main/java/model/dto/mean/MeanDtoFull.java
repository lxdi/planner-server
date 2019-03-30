package model.dto.mean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeanDtoFull extends MeanDtoLazy {

    String criteria;
    Long previd;
    List<Map<String,Object>> layers = new ArrayList<>();

    public List<Map<String, Object>> getLayers() {
        return layers;
    }
    public void setLayers(List<Map<String, Object>> layers) {
        this.layers = layers;
    }

    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Long getPrevid() {
        return previd;
    }
    public void setPrevid(Long previd) {
        this.previd = previd;
    }
}
