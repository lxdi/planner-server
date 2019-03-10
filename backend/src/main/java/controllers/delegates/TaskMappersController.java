package controllers.delegates;

import model.dao.*;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

@Service
public class TaskMappersController {

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

        int i =0, j=0;
        for(; i<layers.size() && j<slots.size(); i++, j++){
            if(isFullReschedule || slots.get(j).getLayer()==null || slots.get(j).getLayer().getId()!=layers.get(i).getId()){
                createTaskMappers(layers.get(i), slots.get(j));
                slots.get(j).setLayer(layers.get(i));
                slotDAO.saveOrUpdate(slots.get(j));
            }
        }

        if(i<layers.size()){
            for(;i<layers.size();i++){
                unassignTasksForLayer(layers.get(i));
            }
        }

        if(j<slots.size()){
            for(;j<slots.size();j++){
                if(slots.get(j).getLayer()!=null) {
                    slots.get(j).setLayer(null);
                    slotDAO.saveOrUpdate(slots.get(j));
                }
            }
        }
    }

    private void createTaskMappers(Layer layerToMap, Slot slot){
        if(layerToMap!=null){
            List<Task> tasks = tasksDAO.tasksByLayer(layerToMap);
            Collections.sort(tasks);
            if(tasks.size()>0) {
                Stack<Task> taskStack = tasksInStack(tasks);
                HQuarter hQuarter = slot.getHquarter();
                List<Week> weeks = weekDAO.weeksOfHquarter(hQuarter);
                List<SlotPosition> slotPositions = slotDAO.getSlotPositionsForSlot(slot);
                Collections.sort(slotPositions);
                Task currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                for(int iw = 0; iw<weeks.size(); iw++){
                    for(int isp=0; isp<slotPositions.size(); isp++){
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

}
