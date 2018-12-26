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
        mapToDto(entity, dto);
        return dto;
    }

    public void mapToDto(HQuarter entity, HquarterDtoLazy dto) {
        dto.setId(entity.getId());
        if(entity.getStartWeek()!=null){
            dto.setStartWeek(entity.getStartWeek());
        }

        if(entity.getEndWeek()!=null){
            dto.setEndWeek(entity.getEndWeek());
        }

        List<Slot> slotList = slotDAO.getSlotsForHquarter(entity);
        if(slotList.size()>0){
            for(Slot slot : slotList){
                dto.getSlotsLazy().add(slotDtoLazyMapper.mapToDto(slot));
            }
        }
    }

    @Override
    public HQuarter mapToEntity(HquarterDtoLazy dto) {
        throw new UnsupportedOperationException();
    }
}
