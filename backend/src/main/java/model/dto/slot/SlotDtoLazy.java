package model.dto.slot;

import java.util.ArrayList;
import java.util.List;

public class SlotDtoLazy {
    Long id;
    long position;
    Long meanid;
    Long hquarterid;
    List<SlotPositionDtoLazy> slotPositions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMeanid() {
        return meanid;
    }

    public void setMeanid(Long meanid) {
        this.meanid = meanid;
    }

    public Long getHquarterid() {
        return hquarterid;
    }

    public void setHquarterid(Long hquarterid) {
        this.hquarterid = hquarterid;
    }

    public List<SlotPositionDtoLazy> getSlotPositions() {
        return slotPositions;
    }

    public void setSlotPositions(List<SlotPositionDtoLazy> slotPositions) {
        this.slotPositions = slotPositions;
    }

    public long getPosition() {
        return position;
    }
    public void setPosition(long position) {
        this.position = position;
    }
}
