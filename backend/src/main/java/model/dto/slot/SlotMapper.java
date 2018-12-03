package model.dto.slot;

import model.dao.IHQuarterDAO;
import model.dao.IMeansDAO;
import model.dao.ISlotDAO;
import model.dto.IMapper;
import model.entities.Slot;
import model.entities.SlotPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlotMapper implements IMapper<SlotDtoLazy, Slot> {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    IHQuarterDAO hquarterDAO;

    @Autowired
    SlotPositionMapper slotPositionMapper;

    @Override
    public SlotDtoLazy mapToDto(Slot entity) {
        SlotDtoLazy dto = new SlotDtoLazy();
        dto.setId(entity.getId());
        if(entity.getHquarter()!=null){
            dto.setHquarterid(entity.getHquarter().getId());
        }
        if(entity.getMean()!=null){
            dto.setMeanid(entity.getMean().getId());
        }

        List<SlotPosition> slotPositionList = slotDAO.getSlotPositionsForSlot(entity);
        if(slotPositionList.size()>0){
            for(SlotPosition slotPosition : slotPositionList){
                dto.getSlotPositions().add(slotPositionMapper.mapToDto(slotPosition));
            }
        }
        return dto;
    }

    @Override
    public Slot mapToEntity(SlotDtoLazy dto) {
        Slot entity = new Slot();
        entity.setId(dto.getId());
        if(dto.getMeanid()>0){
            entity.setMean(meansDAO.meanById(dto.meanid));
        }
        if(dto.getHquarterid()>0){
            entity.setHquarter(hquarterDAO.getById(dto.getHquarterid()));
        }
        return entity;
    }
}
