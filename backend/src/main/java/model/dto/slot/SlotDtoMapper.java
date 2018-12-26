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
public class SlotDtoMapper implements IMapper<SlotDto, Slot> {

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

    @Autowired
    SlotDtoLazyMapper slotDtoLazyMapper;

    @Override
    public SlotDto mapToDto(Slot entity) {
        SlotDto dto = new SlotDto();
        slotDtoLazyMapper.mapToDto(entity, dto);
        if(entity.getHquarter()!=null){
            dto.setHquarterid(entity.getHquarter().getId());
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
    public Slot mapToEntity(SlotDto dto) {
        Slot entity = new Slot();
        entity.setId(dto.getId());
        entity.setPosition(dto.getPosition());

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
