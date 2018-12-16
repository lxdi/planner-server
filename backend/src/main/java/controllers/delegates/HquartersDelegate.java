package controllers.delegates;

import model.dao.*;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
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

    public HquarterDtoLazy update(HquarterDtoLazy hquarterDto){
        HQuarter hQuarter = saveHQuarter(hquarterDto);
        return hquarterMapper.mapToDto(hQuarter);
    }

    public SlotDtoLazy assign(long meanid, long slotid){
        Mean mean = meansDAO.meanById(meanid);
        Slot slot = slotDAO.getById(slotid);
        slot.setMean(mean);
        Layer layer = layerDAO.getNextLayerToSchedule(mean);
        slot.setLayer(layer);
        slotDAO.saveOrUpdate(slot);
        taskMappersController.createTaskMappers(layer, slot);
        return slotMapper.mapToDto(slot);
    }

    public SlotDtoLazy unassign(long slotid){
        Slot slot = slotDAO.getById(slotid);
        Mean mean = slot.getMean();
        Layer layer = slot.getLayer();
        List<Slot> slotsAfter = slotDAO.slotsAfter(slot);

        slot.setMean(null);
        slot.setLayer(null);
        slotDAO.saveOrUpdate(slot);

        for(Slot slotAfter: slotsAfter){
            if(slotAfter.getLayer()!=null) {
                Layer prevLayer = layerDAO.getLayerAtPriority(mean, slotAfter.getLayer().getPriority()-1);
                if(prevLayer!=null){
                    slotAfter.setLayer(prevLayer);
                    taskMappersController.createTaskMappers(prevLayer, slotAfter);
                    slotDAO.saveOrUpdate(slotAfter);
                }
            } else {
                Layer nextLayer = layerDAO.getNextLayerToSchedule(mean);
                if(nextLayer!=null){
                    slotAfter.setLayer(nextLayer);
                    taskMappersController.createTaskMappers(nextLayer, slotAfter);
                    slotDAO.saveOrUpdate(slotAfter);
                }
            }
        }
        return slotMapper.mapToDto(slot);
    }

    public HquarterDtoLazy getDefault(){
        return hquarterMapper.mapToDto(quarterDAO.getDefault());
    }

    public HquarterDtoLazy setDefault(HquarterDtoLazy hquarterDtoLazy){
        assert hquarterDtoLazy.getStartWeek()==null && hquarterDtoLazy.getEndWeek()==null;
        HQuarter defaultHquarter = saveHQuarter(hquarterDtoLazy);
        defaultSettingsPropagator.propagateSettingsFrom(defaultHquarter);
        return hquarterMapper.mapToDto(defaultHquarter);
    }

    private HQuarter saveHQuarter(HquarterDtoLazy hquarterDtoLazy){
        HQuarter hQuarter = hquarterMapper.mapToEntity(hquarterDtoLazy);
        //TODO validate slots before saving
        quarterDAO.saveOrUpdate(hQuarter);
        saveSlots(hquarterDtoLazy.getSlots(), hQuarter.getId());
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
