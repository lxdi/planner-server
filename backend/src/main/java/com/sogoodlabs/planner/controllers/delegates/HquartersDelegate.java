package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.dto.HquarterMapper;
import com.sogoodlabs.planner.model.dto.SlotMapper;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.services.DefaultSettingsPropagator;
import com.sogoodlabs.planner.services.TaskMappersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.DateUtils;
import com.sogoodlabs.planner.services.QuarterGenerator;

import java.sql.Date;
import java.util.*;

@Service
@Transactional
public class HquartersDelegate {

    private static final int TOTAL_NUMBER_OF_HQUARTER_IN_YEAR = 12;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO quarterDAO;

    @Autowired
    ISlotDAO slotDAO;

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

    @Autowired
    HquarterMapper hquarterMapper;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    IMapperExclusionDAO mapperExclusionDAO;

    public List<Map<String, Object>> getAllQuarters(){
        List<Map<String, Object>> result = new ArrayList<>();
        for(HQuarter hQuarter : quarterDAO.getAllHQuartals()){
            result.add(hquarterMapper.mapToDtoLazy(hQuarter));
        }
        return result;
    }

    public List<Map<String, Object>> getCurrentHquarters(){
        List<Map<String, Object>> result = new ArrayList<>();
        List<HQuarter> hQuarters = getCurrentHquarters(DateUtils.currentDate());
        Map<Long, List<Slot>> slotsByHquarterId = new HashMap<>();
        slotDAO.getSlotsForHquarters(hQuarters).forEach(slot -> {
            slotsByHquarterId.putIfAbsent(slot.getHquarter().getId(), new ArrayList<>());
            slotsByHquarterId.get(slot.getHquarter().getId()).add(slot);
        });

        hQuarters.forEach(hq -> result.add(hquarterMapper.mapToDtoLazy(hq, slotsByHquarterId.get(hq.getId()))));
        return result;
    }

    public List<HQuarter> getCurrentHquarters(Date currentDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int currentYear = calendar.get(Calendar.YEAR);

        List<HQuarter> result = new ArrayList<>();
        result.addAll(getOrCreateHquarters(currentYear));
        removeFromHquartersListTillDate(result, currentDate);
        addNextYearHqsIfNeeded(result, currentYear);

        return result;
    }

    public void removeFromHquartersListTillDate(List<HQuarter> hQuarters, Date date){
        if(hQuarters!=null && hQuarters.size()>0){
            List<HQuarter> toRemove = new ArrayList<>();
            for (int i = 0; i < hQuarters.size(); i++) {
                if (DateUtils.differenceInDays(date, hQuarters.get(i).getStartWeek().getStartDay()) < 0
                        && DateUtils.differenceInDays(date, hQuarters.get(i).getEndWeek().getEndDay()) < 0) {
                    toRemove.add(hQuarters.get(i));
                }
            }
            if(toRemove.size()>0){
                toRemove.remove(toRemove.size()-1);
            }
            hQuarters.removeAll(toRemove);
        }
    }

    public List<Map<String, Object>> getCurrentHquartersFull(){
        return getHquartersForGivenYearsOffset(0, 1);
    }

    public List<Map<String, Object>> getHquartersFullCurrentYear(){
        return getHquartersForGivenYearsOffset(0);
    }

    public Map<String, Object> get(long id){
        return hquarterMapper.mapToDtoFull(quarterDAO.getById(id));
    }

    public Map<String, Object> update(Map<String, Object> hquarterDto){
        HQuarter hQuarter = saveHQuarter(hquarterDto);
        return hquarterMapper.mapToDtoFull(hQuarter);
    }

    public Map<String, Object> assign(long meanid, long slotid){
        Mean mean = meansDAO.getOne(meanid);
        Slot slot = slotDAO.getById(slotid);
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);
        cleanExclusions(slot);
        taskMappersService.rescheduleTaskMappers(mean, false);
        return slotMapper.mapToDtoFull(slot);
    }

    public Map<String, Object> unassign(long slotid){
        Slot slot = slotDAO.getById(slotid);
        Mean mean = slot.getMean();
        slot.setLayer(null);
        slot.setMean(null);
        slotDAO.saveOrUpdate(slot);
        cleanExclusions(slot);
        taskMappersService.rescheduleTaskMappers(mean, false);
        return slotMapper.mapToDtoFull(slot);
    }

    public Map<String, Object> getDefault(){
        return hquarterMapper.mapToDtoFull(quarterDAO.getDefault());
    }

    public Map<String, Object> setDefault(Map<String, Object> hquarterDtoFull){
        if(hquarterDtoFull.get("startWeek")!=null || hquarterDtoFull.get("endWeek")!=null){
            throw new UnsupportedOperationException("Not valid hquarter to set for default");
        }
        HQuarter defaultHquarter = saveHQuarter(hquarterDtoFull);
        defaultSettingsPropagator.propagateSettingsFrom(defaultHquarter);
        return hquarterMapper.mapToDtoFull(defaultHquarter);
    }

    public List<Map<String, Object>> getPrev(long currentHqId, int prevCount){
        List<Map<String, Object>> result = new ArrayList<>();
        List<HQuarter> hQuarters = quarterDAO.getPrev(currentHqId, prevCount);
        if(hQuarters.size()==0){
            int year = DateUtils.getYear(quarterDAO.getById(currentHqId).getStartWeek().getStartDay());
            quarterGenerator.generateYear(year-1);
            hQuarters = quarterDAO.getPrev(currentHqId, prevCount);
        }
        Collections.sort(hQuarters);
        for(HQuarter hQuarter : hQuarters){
            result.add(hquarterMapper.mapToDtoLazy(hQuarter));
        }
        return result;
    }

    public List<Map<String, Object>> getNext(long currentHqId, int nextCount){
        List<Map<String, Object>> result = new ArrayList<>();
        List<HQuarter> hQuarters = quarterDAO.getNext(currentHqId, nextCount);
        if(hQuarters.size()==0){
            int year = DateUtils.getYear(quarterDAO.getById(currentHqId).getStartWeek().getStartDay());
            quarterGenerator.generateYear(year+1);
            hQuarters = quarterDAO.getNext(currentHqId, nextCount);
        }
        for(HQuarter hQuarter : hQuarters){
            result.add(hquarterMapper.mapToDtoLazy(hQuarter));
        }
        return result;
    }

    public void pushTasks(long weekid, String dayOfWeekShort){
        taskMappersService.rescheduleTaskMappersWithExclusion(weekid, dayOfWeekShort);
    }

    public void shiftHquarters(long firstHquarterid){
        HQuarter firstHquarter = quarterDAO.getById(firstHquarterid);
        if(firstHquarter!=null){
            int year = DateUtils.getYear(firstHquarter.getStartWeek().getStartDay());
            if(isShiftingAvailable(year)){
                List<HQuarter> hQuartersInYear = quarterDAO.getHQuartersInYear(year);
                for(HQuarter hQuarter : hQuartersInYear){
                    if(DateUtils.differenceInDays(firstHquarter.getStartWeek().getStartDay(), hQuarter.getStartWeek().getStartDay())>=0){
                        hQuarter.setStartWeek(weekDAO.weekByYearAndNumber(year, hQuarter.getStartWeek().getNumber()+1));
                        hQuarter.setEndWeek(weekDAO.weekByYearAndNumber(year, hQuarter.getEndWeek().getNumber()+1));
                        for(Slot slot : slotDAO.getSlotsForHquarter(hQuarter)){
                            cleanExclusions(slot);
                            taskMappersService.rescheduleTaskMappers(slot.getMean(),true);
                        }
                        quarterDAO.saveOrUpdate(hQuarter);
                    }
                }
            } else {
                throw new RuntimeException("Shifting is not available");
            }
        } else {
            throw new RuntimeException("Hquarter doesn't exist");
        }
    }

    private boolean isShiftingAvailable(int year){
        Week lastWeek = weekDAO.lastWeekInYear(year);
        HQuarter lastHquarter = quarterDAO.getLastInYear(year);
        return lastHquarter.getEndWeek().getId()!=lastWeek.getId();
    }

    private HQuarter saveHQuarter(Map<String, Object> hquarterDtoFull){
        HQuarter hQuarter = hquarterMapper.mapToEntity(hquarterDtoFull);
        //TODO validate slots before saving
        quarterDAO.saveOrUpdate(hQuarter);
        saveSlots((List<Map<String, Object>>) hquarterDtoFull.get("slots"), hQuarter.getId());
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

    private void addNextYearHqsIfNeeded(List<HQuarter> hQuarters, int currentYear){
        int hqsToAddAmount = TOTAL_NUMBER_OF_HQUARTER_IN_YEAR-hQuarters.size();
        if(hqsToAddAmount>0){
            List<HQuarter> nextYearHqs = getOrCreateHquarters(currentYear+1);
            for(int i = 0; i<hqsToAddAmount; i++){
                hQuarters.add(nextYearHqs.get(i));
            }
        }
    }

    private List<Map<String, Object>> getHquartersForGivenYearsOffset(int... offsets){
        List<Map<String, Object>> result = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<HQuarter> hQuarters = new ArrayList<>();
        Arrays.stream(offsets).forEach(offset -> hQuarters.addAll(getOrCreateHquarters(currentYear+offset)));

        hQuarters.forEach(hq -> result.add(hquarterMapper.mapToDtoFull(hq)));
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

    private void cleanExclusions(Slot slot){
        List<SlotPosition> slotPositions = slotDAO.getSlotPositionsForSlot(slot);
        mapperExclusionDAO.deleteBySlotPosition(slotPositions);
    }

}
