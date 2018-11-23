package model.dto.layer;

public class LayerDtoLazy {
    long id;
    Long meanid;
    Integer priority;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getMeanid() {
        return meanid;
    }

    public void setMeanid(Long meanid) {
        this.meanid = meanid;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
