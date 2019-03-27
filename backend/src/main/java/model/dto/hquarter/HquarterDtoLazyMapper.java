package model.dto.hquarter;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ISlotDAO;
import model.dto.IMapper;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotDtoLazyMapper;
import model.dto.slot.SlotDtoMapper;
import model.entities.HQuarter;
import model.entities.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class HquarterDtoLazyMapper implements IMapper<HquarterDtoLazy, HQuarter> {

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    SlotDtoLazyMapper slotDtoLazyMapper;

    @Autowired
    CommonMapper commonMapper;

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
            dto.setStartWeek(commonMapper.mapToDto(entity.getStartWeek(), new HashMap<>()));
        }

        if(entity.getEndWeek()!=null){
            dto.setEndWeek(commonMapper.mapToDto(entity.getEndWeek(), new HashMap<>()));
        }

        if(isLoadSlots) {
            addSlots(dto, slotDAO.getSlotsForHquarter(entity));
        }
    }

    public void addSlots(HquarterDtoLazy dto, List<Slot> slots){
        if (slots!=null && slots.size() > 0) {
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
