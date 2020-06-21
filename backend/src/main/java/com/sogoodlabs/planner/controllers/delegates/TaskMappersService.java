package com.sogoodlabs.planner.controllers.delegates;

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


    public void unassignTasksForLayer(Layer layer){
        if(layer!=null) {
            List<Task> tasks = tasksDAO.tasksByLayer(layer);
            for(Task task : tasks){
                TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
                if(taskMapper!=null){
                    taskMapper.setPlanDay(null);
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
                slotDAO.saveOrUpdate(slots.get(i));
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
                    slotDAO.saveOrUpdate(slots.get(j));
                }
            }
        }
    }

    public void rescheduleTaskMappersWithExclusion(long weekid, String dayOfWeekShortForExclusion){
        throw new UnsupportedOperationException();
//        Week week = weekDAO.getById(weekid);
//        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(dayOfWeekShortForExclusion);
//        Set<SlotPosition> slotPositions = new HashSet<>();
//        Set<Slot> slots = new HashSet<>();
//        taskMappersDAO.byWeekAndDay(week, dayOfWeek).forEach(tm -> {
//
//        });
//        slotPositions.forEach(sp -> getOrCreateMapperExclusion(week, sp));
//        slots.forEach(slot -> createTaskMappers(slot.getLayer(), slot));
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
        validateMapping(taskStack.size(), slotPositions, weeks);
        for(Week week : weeks){
            if(!taskStack.isEmpty()){
                createTaskMappersForWeek(week, slotPositions, ()->!taskStack.empty()?taskStack.pop():null);
            } else {
                return;
            }
        }
    }

    private void createTaskMappersForWeek(Week week, List<SlotPosition> slotPositions, Supplier<Task> taskSupplier){
        for (SlotPosition sp : slotPositions) {
            Task currentTask = taskSupplier.get();
            if (currentTask != null) {
                fillTaskMapperForTask(currentTask, week, sp);
            } else {
                return;
            }
        }
    }

    private void validateMapping(int tasksCount, List<SlotPosition> SPs, List<Week> weeks){
        //TODO implement this
    }

    private TaskMapper fillTaskMapperForTask(Task task, Week week, SlotPosition slotPosition){
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        if(taskMapper==null){
            taskMapper = new TaskMapper();
            taskMapper.setTask(task);
        }
        //TODO get the day of week from slotPosition and find this day in the week
        taskMappersDAO.saveOrUpdate(taskMapper);
        return taskMapper;
    }

}
