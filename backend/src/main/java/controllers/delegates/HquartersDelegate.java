package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.*;
import model.dto.SlotMapper;
import model.dto.hquarter.HquarterDtoFull;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterDtoLazyMapper;
import model.dto.hquarter.HquarterDtoFullMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.DateUtils;
import services.QuarterGenerator;

import java.util.*;

@Service
@Transactional
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
    TaskMappersService taskMappersService;

    @Autowired
    DefaultSettingsPropagator defaultSettingsPropagator;

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    SlotMapper slotMapper;

    @Autowired
    CommonMapper commonMapper;

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
        //hQuarters.addAll(getOrCreateHquarters(currentYear+1));

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

    public List<HquarterDtoFull> getCurrentHquartersFull(){
        List<HquarterDtoFull> result = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<HQuarter> hQuarters = new ArrayList<>();
        hQuarters.addAll(getOrCreateHquarters(currentYear));
        hQuarters.addAll(getOrCreateHquarters(currentYear+1));

        for(HQuarter hQuarter : hQuarters){
            HquarterDtoFull dto = hquarterDtoFullMapper.mapToDto(hQuarter);
            result.add(dto);
        }
        return result;
    }

    public List<HquarterDtoFull> getHquartersFullCurrentYear(){
        List<HquarterDtoFull> result = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<HQuarter> hQuarters = new ArrayList<>();
        hQuarters.addAll(getOrCreateHquarters(currentYear));

        for(HQuarter hQuarter : hQuarters){
            HquarterDtoFull dto = hquarterDtoFullMapper.mapToDto(hQuarter);
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

    public Map<String, Object> assign(long meanid, long slotid){
        Mean mean = meansDAO.meanById(meanid);
        Slot slot = slotDAO.getById(slotid);
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);
        taskMappersService.rescheduleTaskMappers(mean, false);
        return slotMapper.mapToDtoFull(slot);
    }

    public Map<String, Object> unassign(long slotid){
        Slot slot = slotDAO.getById(slotid);
        Mean mean = slot.getMean();
        slot.setLayer(null);
        slot.setMean(null);
        slotDAO.saveOrUpdate(slot);
        taskMappersService.rescheduleTaskMappers(mean, false);
        return slotMapper.mapToDtoFull(slot);
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

    private void saveSlots(List<Map<String, Object>> slotsDto, long hquarterid){
        if(slotsDto!=null){
            for(Map<String, Object> slotDto : slotsDto){
                if(slotDto!=null) {
                    slotDto.put("hquarterid", hquarterid);
                    Slot slot = slotMapper.mapToEntity(slotDto);
                    //TODO validate before saving
                    slotDAO.saveOrUpdate(slot);
                    saveSlotPositions((List<Map<String, Object>>) slotDto.get("slotPositions"), slot.getId());
                }
            }
        }
    }

    private void saveSlotPositions(List<Map<String, Object>> slotsPosDto, long slotid){
        if(slotsPosDto!=null && slotsPosDto.size()>0){
            for(Map<String, Object> slotPosDto : slotsPosDto){
                if(slotPosDto!=null) {
                    slotPosDto.put("slotid", slotid);
                    //TODO validate before saving
                    slotDAO.saveOrUpdate(commonMapper.mapToEntity(slotPosDto, new SlotPosition()));
                }
            }
        }
    }

}
