package model.dto.slot;

import model.dto.IMapper;
import model.entities.Slot;
import org.springframework.stereotype.Component;

@Component
public class SlotDtoLazyMapper implements IMapper<SlotDtoLazy, Slot> {

    @Override
    public SlotDtoLazy mapToDto(Slot entity) {
        SlotDtoLazy dto = new SlotDtoLazy();
        mapToDto(entity, dto);
        return dto;
    }

    public void mapToDto(Slot entity, SlotDtoLazy dto){
        dto.setId(entity.getId());
        dto.setPosition(entity.getPosition());
        if(entity.getMean()!=null){
            dto.setMeanid(entity.getMean().getId());
        }
    }

    @Override
    public Slot mapToEntity(SlotDtoLazy dto) {
        throw new UnsupportedOperationException();
    }
}
