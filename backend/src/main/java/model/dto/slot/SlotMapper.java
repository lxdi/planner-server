package model.dto.slot;

import model.dao.IHQuarterDAO;
import model.dao.ILayerDAO;
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
    ILayerDAO layerDAO;

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
        dto.setPosition(entity.getPosition());
        if(entity.getHquarter()!=null){
            dto.setHquarterid(entity.getHquarter().getId());
        }
        if(entity.getMean()!=null){
            dto.setMeanid(entity.getMean().getId());
        }
        if(entity.getLayer()!=null){
            dto.setLayerid(entity.getLayer().getId());
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
        entity.setPosition(dto.getPosition());
        if(dto.getId()!=null){
            entity.setId(dto.getId());
        }
        if(dto.getMeanid()!=null){
            entity.setMean(meansDAO.meanById(dto.meanid));
        }
        if(dto.getLayerid()!=null){
            entity.setLayer(layerDAO.layerById(dto.getLayerid()));
        }
        if(dto.getHquarterid()!=null){
            entity.setHquarter(hquarterDAO.getById(dto.getHquarterid()));
        }
        return entity;
    }
}
