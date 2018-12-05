package model.dto.hquarter;

import model.dao.ISlotDAO;
import model.dto.IMapper;
import model.dto.slot.SlotMapper;
import model.entities.HQuarter;
import model.entities.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HquarterMapper implements IMapper<HquarterDtoLazy, HQuarter> {

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    SlotMapper slotMapper;

    @Override
    public HquarterDtoLazy mapToDto(HQuarter entity) {
        HquarterDtoLazy dto = new HquarterDtoLazy();
        dto.setId(entity.getId());
        dto.setYear(entity.getYear());
        dto.setStartmonth(entity.getStartMonth());
        dto.setStartday(entity.getStartDay());

        List<Slot> slotList = slotDAO.getSlotsForHquarter(entity);
        if(slotList.size()>0){
            for(Slot slot : slotList){
                dto.getSlots().add(slotMapper.mapToDto(slot));
            }
        }
        return dto;
    }

    @Override
    public HQuarter mapToEntity(HquarterDtoLazy dto) {
        HQuarter hquarter = new HQuarter();
        hquarter.setId(dto.getId());
        hquarter.setYear(dto.getYear());
        hquarter.setStartMonth(dto.getStartmonth());
        hquarter.setStartDay(dto.getStartday());
        return hquarter;
    }
}
