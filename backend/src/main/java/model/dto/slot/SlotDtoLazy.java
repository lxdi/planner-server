package model.dto.slot;

import java.util.List;

public class SlotDtoLazy {
    long id;
    long meanid;
    long hquarterid;
    List<SlotPositionLazy> slotPositions;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getMeanid() {
        return meanid;
    }
    public void setMeanid(long meanid) {
        this.meanid = meanid;
    }

    public List<SlotPositionLazy> getSlotPositions() {
        return slotPositions;
    }
    public void setSlotPositions(List<SlotPositionLazy> slotPositions) {
        this.slotPositions = slotPositions;
    }

    public long getHquarterid() {
        return hquarterid;
    }
    public void setHquarterid(long hquarterid) {
        this.hquarterid = hquarterid;
    }
}
