package controllers.delegates;

import model.dao.*;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

@Service
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

    private void createTaskMappers(Layer layerToMap, Slot slot){
        int optimalTasks = 8;
        if(layerToMap!=null){
            List<Task> tasks = tasksDAO.tasksByLayer(layerToMap);
            Collections.sort(tasks);
            int fullWeekMappingUntil = tasks.size()-optimalTasks;
            if(tasks.size()>0) {
                Stack<Task> taskStack = tasksInStack(tasks);
                List<Week> weeks = weekDAO.weeksOfHquarter(slot.getHquarter());
                List<SlotPosition> slotPositions = slotDAO.getSlotPositionsForSlot(slot);
                Collections.sort(slotPositions);
                Task currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                for(int iw = 0; iw<weeks.size(); iw++){
                    for(int isp = 0; isp<slotPositions.size()-ifMappingOnFullWeek(iw, fullWeekMappingUntil); isp++){
                        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(currentTask);
                        if(taskMapper==null){
                            taskMapper = new TaskMapper();
                            taskMapper.setTask(currentTask);
                        }
                        taskMapper.setSlotPosition(slotPositions.get(isp));
                        taskMapper.setWeek(weeks.get(iw));
                        taskMappersDAO.saveOrUpdate(taskMapper);
                        currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                        if(currentTask==null){
                            iw=weeks.size();
                            isp=slotPositions.size();
                        }
                    }
                }
            }
        }
    }

    private int ifMappingOnFullWeek(int weekNumFromZero, int atLeastTo){
        if(weekNumFromZero+1<=atLeastTo){
            return 0;
        } else {
            return 1;
        }
    }

}
