package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.SortUtils;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;

@Service
@Transactional
public class TaskMappersService {

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    SortUtils sortUtils;

    @Autowired
    IMapperExclusionDAO mapperExclusionDAO;

    public void unassignTasksForLayer(Layer layer){
        if(layer!=null) {
            List<Task> tasks = tasksDAO.tasksByLayer(layer);
            for(Task task : tasks){
                TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
                if(taskMapper!=null){
                    taskMapper.setSlotPosition(null);
                    taskMapper.setWeek(null);
                    taskMappersDAO.saveOrUpdate(taskMapper);
                }
            }
        }
    }

    private Stack<Task> tasksInStack(List<Task> tasks){
        Stack<Task> result = new Stack<>();
        if(tasks.size()>0) {
            for (int i = tasks.size() - 1; i >= 0; i--) {
                result.push(tasks.get(i));
            }
        }
        return result;
    }

    public void rescheduleTaskMappers(Mean mean, boolean isFullReschedule){
        List<Layer> layers = layerDAO.getLyersOfMean(mean);
        List<Slot> slots = slotDAO.slotsWithMean(mean);

        int i=0;
        for(; i<layers.size() && i<slots.size(); i++){
            if(isFullReschedule || slots.get(i).getLayer()==null || slots.get(i).getLayer().getId()!=layers.get(i).getId()){
                createTaskMappers(layers.get(i), slots.get(i));
                slots.get(i).setLayer(layers.get(i));
                slotDAO.save(slots.get(i));
            }
        }

        if(i<layers.size()){
            for(int j = i;j<layers.size();j++){
                unassignTasksForLayer(layers.get(j));
            }
        }

        if(i<slots.size()){
            for(int j = i;j<slots.size();j++){
                if(slots.get(j).getLayer()!=null) {
                    slots.get(j).setLayer(null);
                    slotDAO.save(slots.get(j));
                }
            }
        }
    }

    public void rescheduleTaskMappersWithExclusion(long weekid, String dayOfWeekShortForExclusion){
        Week week = weekDAO.getById(weekid);
        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(dayOfWeekShortForExclusion);
        Set<SlotPosition> slotPositions = new HashSet<>();
        Set<Slot> slots = new HashSet<>();
        taskMappersDAO.byWeekAndDay(week, dayOfWeek).forEach(tm -> {
            slotPositions.add(tm.getSlotPosition());
            slots.add(tm.getSlotPosition().getSlot());
        });
        slotPositions.forEach(sp -> getOrCreateMapperExclusion(week, sp));
        slots.forEach(slot -> createTaskMappers(slot.getLayer(), slot));
    }

    private MapperExclusion getOrCreateMapperExclusion(Week week, SlotPosition sp){
        MapperExclusion mapperExclusion = mapperExclusionDAO.getByWeekBySP(week, sp);
        if(mapperExclusion==null){
            mapperExclusion = new MapperExclusion();
            mapperExclusion.setWeek(week);
            mapperExclusion.setSlotPosition(sp);
            mapperExclusion = mapperExclusionDAO.save(mapperExclusion);
        }
        return mapperExclusion;
    }

    public void createTaskMappers(Layer layerToMap, Slot slot){
        if(layerToMap!=null){
            List<Task> tasks = sortUtils.sortTasks(tasksDAO.tasksByLayer(layerToMap));
            if(tasks.size()>0) {
                List<SlotPosition> slotPositions = sortUtils.sortSlotPositions(slotDAO.getSlotPositionsForSlot(slot));
                createTaskMappers(weekDAO.weeksOfHquarter(slot.getHquarter()), slotPositions, tasksInStack(tasks));
            }
        }
    }

    private void createTaskMappers(List<Week> weeks, List<SlotPosition> slotPositions, Stack<Task> taskStack){
        List<MapperExclusion> exclusions = mapperExclusionDAO.getByWeeksBySPs(weeks, slotPositions);
        validateMapping(taskStack.size(), slotPositions, weeks, exclusions);
        for(Week week : weeks){
            if(!taskStack.isEmpty()){
                createTaskMappersForWeek(week, slotPositions, ()->!taskStack.empty()?taskStack.pop():null, exclusions);
            } else {
                return;
            }
        }
    }

    private void createTaskMappersForWeek(Week week, List<SlotPosition> slotPositions, Supplier<Task> taskSupplier, List<MapperExclusion> exclusions){
        for(SlotPosition sp : slotPositions){
            if(checkExclusions(exclusions, week, sp)){
                Task currentTask = taskSupplier.get();
                if(currentTask!=null){
                    fillTaskMapperForTask(currentTask, week, sp);
                } else {
                    return;
                }
            }
        }
    }

    private void validateMapping(int tasksCount, List<SlotPosition> SPs, List<Week> weeks, List<MapperExclusion> exclusions){
        if((SPs.size()*weeks.size()-exclusions.size())<tasksCount){
            throw new UnsupportedOperationException("There's not enough place to schedule the all Tasks");
        }
    }

    private TaskMapper fillTaskMapperForTask(Task task, Week week, SlotPosition slotPosition){
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        if(taskMapper==null){
            taskMapper = new TaskMapper();
            taskMapper.setTask(task);
        }
        taskMapper.setSlotPosition(slotPosition);
        taskMapper.setWeek(week);
        taskMappersDAO.saveOrUpdate(taskMapper);
        return taskMapper;
    }

    private boolean checkExclusions(List<MapperExclusion> exclusions, Week week, SlotPosition slotPosition){
        if(exclusions==null || exclusions.size()==0){
            return true;
        }
        for(MapperExclusion me : exclusions){
            if(me.getWeek().getId()==week.getId() && me.getSlotPosition().getId() == slotPosition.getId()) {
                return false;
            }
        }
        return true;
    }

}
