package controllers.delegates;

import model.dao.*;
import model.dto.hquarter.HquarterDtoFull;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterDtoLazyMapper;
import model.dto.hquarter.HquarterDtoFullMapper;
import model.dto.slot.SlotDto;
import model.dto.slot.SlotDtoMapper;
import model.dto.slot.SlotPositionDtoLazy;
import model.dto.slot.SlotPositionMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.DateUtils;
import services.QuarterGenerator;

import java.util.*;

@Service
public class HquartersDelegate {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO quarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    HquarterDtoLazyMapper hquarterDtoLazyMapper;

    @Autowired
    HquarterDtoFullMapper hquarterDtoFullMapper;

    @Autowired
    SlotDtoMapper slotDtoMapper;

    @Autowired
    SlotPositionMapper slotPositionMapper;

    @Autowired
    TaskMappersController taskMappersController;

    @Autowired
    DefaultSettingsPropagator defaultSettingsPropagator;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    QuarterGenerator quarterGenerator;

    public List<HquarterDtoLazy> getAllQuarters(){
        List<HquarterDtoLazy> result = new ArrayList<>();
        for(HQuarter hQuarter : quarterDAO.getAllHQuartals()){
            result.add(hquarterDtoLazyMapper.mapToDto(hQuarter));
        }
        return result;
    }

    public List<HquarterDtoLazy> getCurrentHquarters(){
        List<HquarterDtoLazy> result = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<HQuarter> hQuarters = new ArrayList<>();
        hQuarters.addAll(getOrCreateHquarters(currentYear));
        hQuarters.addAll(getOrCreateHquarters(currentYear+1));

        Map<Long, List<Slot>> slotsByHquarterId = new HashMap<>();
        for(Slot slot : slotDAO.getSlotsForHquarters(hQuarters)){
            slotsByHquarterId.putIfAbsent(slot.getHquarter().getId(), new ArrayList<>());
            slotsByHquarterId.get(slot.getHquarter().getId()).add(slot);
        }
        for(HQuarter hQuarter : hQuarters){
            HquarterDtoLazy dto = hquarterDtoLazyMapper.mapToDtoWithoutSlots(hQuarter);
            hquarterDtoLazyMapper.addSlots(dto, slotsByHquarterId.get(hQuarter.getId()));
            result.add(dto);
        }
        return result;
    }

    private List<HQuarter> getOrCreateHquarters(int year){
        List<HQuarter> result = quarterDAO.getHQuartersInYear(year);
        if(result.size()==0){
            quarterGenerator.generateYear(year);
            result = quarterDAO.getHQuartersInYear(year);
        }
        return result;
    }

    public HquarterDtoFull get(long id){
        return hquarterDtoFullMapper.mapToDto(quarterDAO.getById(id));
    }

    public HquarterDtoFull update(HquarterDtoFull hquarterDto){
        HQuarter hQuarter = saveHQuarter(hquarterDto);
        return hquarterDtoFullMapper.mapToDto(hQuarter);
    }

    public SlotDto assign(long meanid, long slotid){
        Mean mean = meansDAO.meanById(meanid);
        Slot slot = slotDAO.getById(slotid);
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);
        taskMappersController.rescheduleTaskMappers(mean, false);
        return slotDtoMapper.mapToDto(slot);
    }

    public SlotDto unassign(long slotid){
        Slot slot = slotDAO.getById(slotid);
        Mean mean = slot.getMean();
        slot.setLayer(null);
        slot.setMean(null);
        slotDAO.saveOrUpdate(slot);
        taskMappersController.rescheduleTaskMappers(mean, false);
        return slotDtoMapper.mapToDto(slot);
    }

    public HquarterDtoFull getDefault(){
        return hquarterDtoFullMapper.mapToDto(quarterDAO.getDefault());
    }

    public HquarterDtoFull setDefault(HquarterDtoFull hquarterDtoFull){
        assert hquarterDtoFull.getStartWeek()==null && hquarterDtoFull.getEndWeek()==null;
        HQuarter defaultHquarter = saveHQuarter(hquarterDtoFull);
        defaultSettingsPropagator.propagateSettingsFrom(defaultHquarter);
        return hquarterDtoFullMapper.mapToDto(defaultHquarter);
    }

    public List<HquarterDtoLazy> getPrev(long currentHqId, int prevCount){
        List<HquarterDtoLazy> result = new ArrayList<>();
        List<HQuarter> hQuarters = quarterDAO.getPrev(currentHqId, prevCount);
        if(hQuarters.size()==0){
            int year = DateUtils.getYear(quarterDAO.getById(currentHqId).getStartWeek().getStartDay());
            quarterGenerator.generateYear(year-1);
            hQuarters = quarterDAO.getPrev(currentHqId, prevCount);
        }
        Collections.sort(hQuarters);
        for(HQuarter hQuarter : hQuarters){
            result.add(hquarterDtoLazyMapper.mapToDto(hQuarter));
        }
        return result;
    }

    public List<HquarterDtoLazy> getNext(long currentHqId, int nextCount){
        List<HquarterDtoLazy> result = new ArrayList<>();
        List<HQuarter> hQuarters = quarterDAO.getNext(currentHqId, nextCount);
        if(hQuarters.size()==0){
            int year = DateUtils.getYear(quarterDAO.getById(currentHqId).getStartWeek().getStartDay());
            quarterGenerator.generateYear(year+1);
            hQuarters = quarterDAO.getNext(currentHqId, nextCount);
        }
        for(HQuarter hQuarter : hQuarters){
            result.add(hquarterDtoLazyMapper.mapToDto(hQuarter));
        }
        return result;
    }


    private HQuarter saveHQuarter(HquarterDtoFull hquarterDtoFull){
        HQuarter hQuarter = hquarterDtoFullMapper.mapToEntity(hquarterDtoFull);
        //TODO validate slots before saving
        quarterDAO.saveOrUpdate(hQuarter);
        saveSlots(hquarterDtoFull.getSlots(), hQuarter.getId());
        return hQuarter;
    }

    private void saveSlots(List<SlotDto> slotsDto, long hquarterid){
        if(slotsDto!=null){
            for(SlotDto slotDto : slotsDto){
                if(slotDto!=null) {
                    slotDto.setHquarterid(hquarterid);
                    Slot slot = slotDtoMapper.mapToEntity(slotDto);
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
