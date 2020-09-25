package com.sogoodlabs.planner.model.dto;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HquarterMapper {

    private static final String SLOTS_FIELD_TITLE = "slots";
    private static final String SLOTSLAZY_FIELD_TITLE = "slotsLazy";
    private static final String STARTWEEK_FIELD_TITLE = "startWeek";
    private static final String ENDWEEK_FIELD_TITLE = "endWeek";
    private static final String STARTDAY_FIELD_TITLE = "startDay";
    private static final String END_FIELD_TITLE = "endDay";
    private static final String DAYS_FIELD_TITLE = "days";
    private static final String WEEKS_FIELD_TITLE = "weeks";
    private static final String NUMBER_OF_REPETITIONS = "repsCount";
    private static final String NUMBER_OF_REPETITIONS_ONLY = "repsOnlyCount";

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    SlotMapper slotMapper;

    @Autowired
    WeekDao weekDao;

    @Autowired
    TaskMapperDao taskMapperDao;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    IRepDAO repDAO;

    public Map<String, Object> mapToDtoLazy(HQuarter hQuarter){
        return mapToDtoLazy(hQuarter, slotDAO.getSlotsForHquarter(hQuarter));
    }

    public Map<String, Object> mapToDtoLazy(HQuarter hQuarter, List<Slot> slots){
        Map<String, Object> dto = commonMapper.mapToDto(hQuarter);
        dto.putIfAbsent(SLOTSLAZY_FIELD_TITLE, new ArrayList<>());
        if(slots!=null){
            slots.forEach(slot -> ((List)dto.get(SLOTSLAZY_FIELD_TITLE)).add(slotMapper.mapToDtoLazy(slot)));
        }
        if(hQuarter.getStartWeek()!=null) {
            dto.put(STARTWEEK_FIELD_TITLE, commonMapper.mapToDto(hQuarter.getStartWeek()));
        }
        if(hQuarter.getEndWeek()!=null) {
            dto.put(ENDWEEK_FIELD_TITLE, commonMapper.mapToDto(hQuarter.getEndWeek()));
        }
        if(hQuarter.getStartWeek()!=null && hQuarter.getEndWeek()!=null){
            dto.put(NUMBER_OF_REPETITIONS,
                    repDAO.numberOfRepetitionsInRange(
                            hQuarter.getStartWeek().getStartDay(), hQuarter.getEndWeek().getEndDay(), false));
            dto.put(NUMBER_OF_REPETITIONS_ONLY,
                    repDAO.numberOfRepetitionsInRange(
                            hQuarter.getStartWeek().getStartDay(), hQuarter.getEndWeek().getEndDay(), true));
        }
        return dto;
    }

    public Map<String, Object> mapToDtoFull(HQuarter hQuarter){
        List<Slot> slots = slotDAO.getSlotsForHquarter(hQuarter);
        Map<String, Object> dto = mapToDtoLazy(hQuarter, slots);
        dto.putIfAbsent(SLOTS_FIELD_TITLE, new ArrayList<>());
        slots.forEach(slot -> ((List)dto.get(SLOTS_FIELD_TITLE)).add(slotMapper.mapToDtoFull(slot)));
        fillWeekWithTasks(dto, hQuarter);
        return dto;
    }


    public HQuarter mapToEntity(Map<String, Object> dto){
        HQuarter hQuarter = commonMapper.mapToEntity(dto, new HQuarter());
        if(hQuarter.getStartWeek()==null && dto.get(STARTWEEK_FIELD_TITLE)!=null){
            hQuarter.setStartWeek(weekDao.getById(Long.parseLong(""+((Map)dto.get(STARTWEEK_FIELD_TITLE)).get("id"))));
        }
        if(hQuarter.getEndWeek()==null && dto.get(ENDWEEK_FIELD_TITLE)!=null){
            hQuarter.setEndWeek(weekDao.getById(Long.parseLong(""+((Map)dto.get(ENDWEEK_FIELD_TITLE)).get("id"))));
        }
        return hQuarter;
    }

    /** Add weeks field, that's a list of
     * {
     *     startDay: startDay of week,
     *     endDay: endDay of week,
     *     days: List of {
     *         dayOfWeek: List of {task Dto Full}
     *     }
     * }
     *
     * */
    private void fillWeekWithTasks(Map<String, Object> dto, HQuarter entity){
        if(entity.getStartWeek()!=null && entity.getEndWeek()!=null) {
            List<Week> weeks = weekDao.weeksOfHquarter(entity);
            List<Slot> slots = slotDAO.getSlotsForHquarter(entity);
            List<SlotPosition> slotPositionList = new ArrayList<>();
            for(Slot slot : slots){
                slotPositionList.addAll(slotDAO.getSlotPositionsForSlot(slot));
            }
            List<TaskMapper> taskMappers = taskMapperDao.taskMappersByWeeksAndSlotPositions(weeks, slotPositionList);
            Map<Long, List<TaskMapper>> taskMappersByWeekId = new HashMap<>();
            for(TaskMapper taskMapper : taskMappers){
                taskMappersByWeekId.putIfAbsent(taskMapper.getWeek().getId(), new ArrayList<>());
                taskMappersByWeekId.get(taskMapper.getWeek().getId()).add(taskMapper);
            }
            dto.putIfAbsent(WEEKS_FIELD_TITLE, new ArrayList<>());
            for (Week week : weeks) {
                ((List)dto.get(WEEKS_FIELD_TITLE)).add(mapWeeksWithTasksDto(week, taskMappersByWeekId));
            }
        }
    }

    private Map<String, Object> mapWeeksWithTasksDto(Week week, Map<Long, List<TaskMapper>> taskMappersByWeekId){
        Map<String, Object> dto = commonMapper.mapToDto(week);
//        dto.put(STARTDAY_FIELD_TITLE, DateUtils.fromDate(week.getStartDay()));
//        dto.put(END_FIELD_TITLE, DateUtils.fromDate(week.getEndDay()));
        dto.putIfAbsent(DAYS_FIELD_TITLE, new HashMap<>());
        if(taskMappersByWeekId.get(week.getId())!=null){
            for(TaskMapper taskMapper : taskMappersByWeekId.get(week.getId())){
                ((Map)dto.get(DAYS_FIELD_TITLE)).putIfAbsent(taskMapper.getSlotPosition().getDayOfWeek().name(), new ArrayList<>());
                ((List)((Map)dto.get(DAYS_FIELD_TITLE)).get(taskMapper.getSlotPosition().getDayOfWeek().name()))
                        .add(tasksDtoMapper.mapToDtoFull(taskMapper.getTask()));
            }
        }
        dto.put(NUMBER_OF_REPETITIONS,
                repDAO.numberOfRepetitionsInRange(week.getStartDay(), week.getEndDay(), false));
        dto.put(NUMBER_OF_REPETITIONS_ONLY,
                repDAO.numberOfRepetitionsInRange(week.getStartDay(), week.getEndDay(), true));
        return dto;
    }


}
