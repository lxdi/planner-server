package model.dto.hquarter;

import model.dao.ISlotDAO;
import model.dao.ITaskMappersDAO;
import model.dao.IWeekDAO;
import model.dto.IMapper;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotDtoMapper;
import model.dto.task.TasksDtoMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HquarterDtoFullMapper implements IMapper<HquarterDtoFull, HQuarter> {

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    SlotDtoMapper slotDtoMapper;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    HquarterDtoLazyMapper hquarterDtoLazyMapper;

    @Override
    public HquarterDtoFull mapToDto(HQuarter entity) {
        HquarterDtoFull dto = new HquarterDtoFull();
        hquarterDtoLazyMapper.mapToDto(entity, dto);
        List<Slot> slotList = slotDAO.getSlotsForHquarter(entity);
        if(slotList.size()>0){
            for(Slot slot : slotList){
                dto.getSlots().add(slotDtoMapper.mapToDto(slot));
            }
        }
        fillWeekWithTasks(dto, entity);
        return dto;
    }

    private void fillWeekWithTasks(HquarterDtoFull dto, HQuarter entity){
        if(entity.getStartWeek()!=null && entity.getEndWeek()!=null) {
            List<Week> weeks = weekDAO.weeksOfHquarter(entity);
            List<Slot> slots = slotDAO.getSlotsForHquarter(entity);
            List<SlotPosition> slotPositionList = new ArrayList<>();
            for(Slot slot : slots){
                slotPositionList.addAll(slotDAO.getSlotPositionsForSlot(slot));
            }
            List<TaskMapper> taskMappers = taskMappersDAO.taskMappersByWeeksAndSlotPositions(weeks, slotPositionList);
            Map<Long, List<TaskMapper>> taskMappersByWeekId = new HashMap<>();
            for(TaskMapper taskMapper : taskMappers){
                taskMappersByWeekId.putIfAbsent(taskMapper.getWeek().getId(), new ArrayList<>());
                taskMappersByWeekId.get(taskMapper.getWeek().getId()).add(taskMapper);
            }
            for (Week week : weeks) {
                WeekWithTasksDto weekWithTasksDto = new WeekWithTasksDto();
                weekWithTasksDto.setStartDay(week.getStartDay());
                if(taskMappersByWeekId.get(week.getId())!=null){
                    for(TaskMapper taskMapper : taskMappersByWeekId.get(week.getId())){
                        if (weekWithTasksDto.getDays().get(taskMapper.getSlotPosition().getDaysOfWeek()) == null) {
                            weekWithTasksDto.getDays().put(taskMapper.getSlotPosition().getDaysOfWeek(), new ArrayList<>());
                        }
                        weekWithTasksDto.getDays().get(taskMapper.getSlotPosition().getDaysOfWeek()).add(tasksDtoMapper.mapToDto(taskMapper.getTask()));
                    }
                }
                dto.getWeeks().add(weekWithTasksDto);
            }
        }
    }

//    private void fillWeekWithTasks(HquarterDtoFull dto, HQuarter entity){
//        if(entity.getStartWeek()!=null && entity.getEndWeek()!=null) {
//            List<Week> weeks = weekDAO.weeksOfHquarter(entity);
//            List<Slot> slots = slotDAO.getSlotsForHquarter(entity);
//            Map<Slot, List<SlotPosition>> slotPositions = new HashMap<>();
//            for(Slot slot : slots){
//                slotPositions.putIfAbsent(slot, new ArrayList<>());
//                slotPositions.get(slot).addAll(slotDAO.getSlotPositionsForSlot(slot));
//            }
//            for (Week week : weeks) {
//                WeekWithTasksDto weekWithTasksDto = new WeekWithTasksDto();
//                weekWithTasksDto.setStartDay(week.getStartDay());
//                for (Slot slot : slots) {
//                    for (SlotPosition slotPosition : slotPositions.get(slot)) {
//                        TaskMapper taskMapper = taskMappersDAO.taskMapperByWeekAndSlotPosition(week, slotPosition);
//                        if(taskMapper!=null) {
//                            if (weekWithTasksDto.getDays().get(taskMapper.getSlotPosition().getDaysOfWeek()) == null) {
//                                weekWithTasksDto.getDays().put(taskMapper.getSlotPosition().getDaysOfWeek(), new ArrayList<>());
//                            }
//                            weekWithTasksDto.getDays().get(taskMapper.getSlotPosition().getDaysOfWeek()).add(tasksDtoMapper.mapToDto(taskMapper.getTask()));
//                        }
//                    }
//                }
//                dto.getWeeks().add(weekWithTasksDto);
//            }
//        }
//    }

    @Override
    public HQuarter mapToEntity(HquarterDtoFull dto) {
        HQuarter hquarter = new HQuarter();
        hquarter.setId(dto.getId());
        if(dto.getStartWeek()!=null) {
            hquarter.setStartWeek(dto.getStartWeek());
        }
        if(dto.getEndWeek()!=null) {
            hquarter.setEndWeek(dto.getEndWeek());
        }
        return hquarter;
    }
}
