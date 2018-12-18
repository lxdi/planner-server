package model.dto.hquarter;

import model.dao.ISlotDAO;
import model.dao.ITaskMappersDAO;
import model.dao.IWeekDAO;
import model.dto.IMapper;
import model.dto.slot.SlotLazyTemp;
import model.dto.slot.SlotMapper;
import model.dto.task.TasksDtoMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HquarterMapperFull implements IMapper<HquarterDtoFull, HQuarter> {

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    SlotMapper slotMapper;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Override
    public HquarterDtoFull mapToDto(HQuarter entity) {

        //TODO use mapper for the lazy
        HquarterDtoFull dto = new HquarterDtoFull();
        dto.setId(entity.getId());
        if(entity.getStartWeek()!=null){
            dto.setStartWeek(entity.getStartWeek());
        }

        if(entity.getEndWeek()!=null){
            dto.setEndWeek(entity.getEndWeek());
        }

        List<Slot> slotList = slotDAO.getSlotsForHquarter(entity);
        if(slotList.size()>0){
            for(Slot slot : slotList){
                dto.getSlotsLazy().add(
                        new SlotLazyTemp(slot.getId(), slot.getPosition(), slot.getMean()!=null? slot.getMean().getId():null));
            }
            for(Slot slot : slotList){
                //dto.getSlotsLazy().put(slot.getId(), slot.getPosition());
                dto.getSlots().add(slotMapper.mapToDto(slot));
            }
        }

        if(entity.getStartWeek()!=null && entity.getEndWeek()!=null) {
            List<Week> weeks = weekDAO.weeksOfHquarter(entity);
            for (Week week : weeks) {
                WeekWithTasksDto weekWithTasksDto = new WeekWithTasksDto();
                for (Slot slot : slotDAO.getSlotsForHquarter(entity)) {
                    for (SlotPosition slotPosition : slotDAO.getSlotPositionsForSlot(slot)) {
                        TaskMapper taskMapper = taskMappersDAO.taskMapperByWeekAndSlotPosition(week, slotPosition);
                        if(taskMapper!=null) {
                            if (weekWithTasksDto.getDays().get(taskMapper.getSlotPosition().getDaysOfWeek()) == null) {
                                weekWithTasksDto.getDays().put(taskMapper.getSlotPosition().getDaysOfWeek(), new ArrayList<>());
                            }
                            weekWithTasksDto.getDays().get(taskMapper.getSlotPosition().getDaysOfWeek()).add(tasksDtoMapper.mapToDto(taskMapper.getTask()));
                        }
                    }
                }
                dto.getWeeks().add(weekWithTasksDto);
            }
        }

        return dto;
    }

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
