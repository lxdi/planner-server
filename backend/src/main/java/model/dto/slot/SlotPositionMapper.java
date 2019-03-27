package model.dto.slot;

import model.dao.ISlotDAO;
import model.dto.IMapper;
import model.entities.SlotPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SlotPositionMapper implements IMapper<SlotPositionDtoLazy, SlotPosition> {

    @Autowired
    ISlotDAO slotDAO;

    @Override
    public SlotPositionDtoLazy mapToDto(SlotPosition entity) {
        SlotPositionDtoLazy dto = new SlotPositionDtoLazy();
        dto.setId(entity.getId());
        dto.setDayOfWeek(entity.getDaysOfWeek());
        dto.setPosition(entity.getPosition());
        if(entity.getSlot()!=null){
            dto.setSlotid(entity.getSlot().getId());
        }
        return dto;
    }

    @Override
    public SlotPosition mapToEntity(SlotPositionDtoLazy dto) {
        SlotPosition entity = new SlotPosition();
        if(dto.getId()!=null) {
            entity.setId(dto.getId());
        }
        entity.setDaysOfWeek(dto.getDayOfWeek());
        entity.setPosition(dto.getPosition());
        if(dto.getSlotid()>0){
            entity.setSlot(slotDAO.getById(dto.getSlotid()));
        }
        return entity;
    }
}
