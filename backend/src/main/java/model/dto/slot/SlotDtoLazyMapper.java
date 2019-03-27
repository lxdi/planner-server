package model.dto.slot;

import model.dao.ILayerDAO;
import model.dto.IMapper;
import model.entities.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SlotDtoLazyMapper implements IMapper<SlotDtoLazy, Slot> {

    @Autowired
    ILayerDAO layerDAO;

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
        if(entity.getLayer()!=null){
            dto.setTasksInLayer(layerDAO.taskCountInLayer(entity.getLayer()));
        }
    }

    @Override
    public Slot mapToEntity(SlotDtoLazy dto) {
        throw new UnsupportedOperationException();
    }
}
