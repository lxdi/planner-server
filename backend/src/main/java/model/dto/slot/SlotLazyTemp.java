package model.dto.slot;

public class SlotLazyTemp {

    long id;
    int position;
    Long meanid;
    public SlotLazyTemp(long id, int position, Long meanid){
        this.id = id;
        this.position = position;
        this.meanid = meanid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Long getMeanid() {
        return meanid;
    }

    public void setMeanid(Long meanid) {
        this.meanid = meanid;
    }
}
