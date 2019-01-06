package model.dto.hquarter;

import model.dao.ISlotDAO;
import model.dto.IMapper;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotDtoLazyMapper;
import model.dto.slot.SlotDtoMapper;
import model.entities.HQuarter;
import model.entities.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HquarterDtoLazyMapper implements IMapper<HquarterDtoLazy, HQuarter> {

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    SlotDtoLazyMapper slotDtoLazyMapper;

    @Override
    public HquarterDtoLazy mapToDto(HQuarter entity) {
        HquarterDtoLazy dto = new HquarterDtoLazy();
        mapToDto(entity, dto, true);
        return dto;
    }

    public HquarterDtoLazy mapToDtoWithoutSlots(HQuarter entity) {
        HquarterDtoLazy dto = new HquarterDtoLazy();
        mapToDto(entity, dto, false);
        return dto;
    }

    public void mapToDto(HQuarter entity, HquarterDtoLazy dto, boolean isLoadSlots) {
        dto.setId(entity.getId());
        if(entity.getStartWeek()!=null){
            dto.setStartWeek(entity.getStartWeek());
        }

        if(entity.getEndWeek()!=null){
            dto.setEndWeek(entity.getEndWeek());
        }

        if(isLoadSlots) {
            addSlots(dto, slotDAO.getSlotsForHquarter(entity));
        }
    }

    public void addSlots(HquarterDtoLazy dto, List<Slot> slots){
        if (slots.size() > 0) {
            for (Slot slot : slots) {
                dto.getSlotsLazy().add(slotDtoLazyMapper.mapToDto(slot));
            }
        }
    }

    @Override
    public HQuarter mapToEntity(HquarterDtoLazy dto) {
        throw new UnsupportedOperationException();
    }
}
