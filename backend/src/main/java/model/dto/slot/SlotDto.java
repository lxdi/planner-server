package model.dto.slot;

import java.util.ArrayList;
import java.util.List;

public class SlotDto extends SlotDtoLazy {

    Long layerid;
    Long hquarterid;
    List<SlotPositionDtoLazy> slotPositions = new ArrayList<>();

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

    public Long getLayerid() {
        return layerid;
    }

    public void setLayerid(Long layerid) {
        this.layerid = layerid;
    }
}
