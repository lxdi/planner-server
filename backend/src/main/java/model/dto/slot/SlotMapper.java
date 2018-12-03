package model.dto.slot;

import model.dao.IMeansDAO;
import model.dto.IMapper;
import model.entities.Slot;
import org.springframework.beans.factory.annotation.Autowired;

public class SlotMapper implements IMapper<SlotDtoLazy, Slot> {

    @Autowired
    IMeansDAO meansDAO;

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

        //TODO fill slot positions

        return dto;
    }

    @Override
    public Slot mapToEntity(SlotDtoLazy dto) {
        Slot entity = new Slot();
        entity.setId(dto.getId());
        if(dto.getMeanid()>0){
            entity.setMean(meansDAO.meanById(dto.meanid));
        }
        //TODO fill HQuarter
        return entity;
    }
}
