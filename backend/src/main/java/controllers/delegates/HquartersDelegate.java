package controllers.delegates;

import model.dao.*;
import model.dto.hquarter.HquarterDtoFull;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
import model.dto.hquarter.HquarterMapperFull;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotMapper;
import model.dto.slot.SlotPositionDtoLazy;
import model.dto.slot.SlotPositionMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HquartersDelegate {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO quarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    @Autowired
    HquarterMapperFull hquarterMapperFull;

    @Autowired
    SlotMapper slotMapper;

    @Autowired
    SlotPositionMapper slotPositionMapper;

    @Autowired
    TaskMappersController taskMappersController;

    @Autowired
    DefaultSettingsPropagator defaultSettingsPropagator;

    @Autowired
    ILayerDAO layerDAO;

    public List<HquarterDtoLazy> getAllQuarters(){
        List<HquarterDtoLazy> result = new ArrayList<>();
        for(HQuarter hQuarter : quarterDAO.getAllHQuartals()){
            result.add(hquarterMapper.mapToDto(hQuarter));
        }
        return result;
    }

    public HquarterDtoFull get(long id){
        return hquarterMapperFull.mapToDto(quarterDAO.getById(id));
    }

    public HquarterDtoFull update(HquarterDtoFull hquarterDto){
        HQuarter hQuarter = saveHQuarter(hquarterDto);
        return hquarterMapperFull.mapToDto(hQuarter);
    }

    public SlotDtoLazy assign(long meanid, long slotid){
        Mean mean = meansDAO.meanById(meanid);
        Slot slot = slotDAO.getById(slotid);
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);
        taskMappersController.rescheduleTaskMappers(mean, false);
        return slotMapper.mapToDto(slot);
    }

    public SlotDtoLazy unassign(long slotid){
        Slot slot = slotDAO.getById(slotid);
        Mean mean = slot.getMean();
        slot.setLayer(null);
        slot.setMean(null);
        slotDAO.saveOrUpdate(slot);
        taskMappersController.rescheduleTaskMappers(mean, false);
        return slotMapper.mapToDto(slot);
    }

    public HquarterDtoFull getDefault(){
        return hquarterMapperFull.mapToDto(quarterDAO.getDefault());
    }

    public HquarterDtoFull setDefault(HquarterDtoFull hquarterDtoFull){
        assert hquarterDtoFull.getStartWeek()==null && hquarterDtoFull.getEndWeek()==null;
        HQuarter defaultHquarter = saveHQuarter(hquarterDtoFull);
        defaultSettingsPropagator.propagateSettingsFrom(defaultHquarter);
        return hquarterMapperFull.mapToDto(defaultHquarter);
    }

    private HQuarter saveHQuarter(HquarterDtoFull hquarterDtoFull){
        HQuarter hQuarter = hquarterMapperFull.mapToEntity(hquarterDtoFull);
        //TODO validate slots before saving
        quarterDAO.saveOrUpdate(hQuarter);
        saveSlots(hquarterDtoFull.getSlots(), hQuarter.getId());
        return hQuarter;
    }

    private void saveSlots(List<SlotDtoLazy> slotsDto, long hquarterid){
        if(slotsDto!=null){
            for(SlotDtoLazy slotDto : slotsDto){
                if(slotDto!=null) {
                    slotDto.setHquarterid(hquarterid);
                    Slot slot = slotMapper.mapToEntity(slotDto);
                    //TODO validate before saving
                    slotDAO.saveOrUpdate(slot);
                    saveSlotPositions(slotDto.getSlotPositions(), slot.getId());
                }
            }
        }
    }

    private void saveSlotPositions(List<SlotPositionDtoLazy> slotsPosDto, long slotid){
        if(slotsPosDto!=null && slotsPosDto.size()>0){
            for(SlotPositionDtoLazy slotPosDto : slotsPosDto){
                if(slotPosDto!=null) {
                    slotPosDto.setSlotid(slotid);
                    //TODO validate before saving
                    slotDAO.saveOrUpdate(slotPositionMapper.mapToEntity(slotPosDto));
                }
            }
        }
    }

}
