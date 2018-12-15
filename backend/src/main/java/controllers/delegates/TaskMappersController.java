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

    public void createTaskMappers(Mean mean, Slot slot){
        Layer layerToMap = layerDAO.getNextLayerToSchedule(mean);
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
                for(int iw = 0; iw<weeks.size(); iw=iw+2){
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

    private Stack<Task> tasksInStack(List<Task> tasks){
        Stack<Task> result = new Stack<>();
        if(tasks.size()>0) {
            for (int i = tasks.size() - 1; i >= 0; i--) {
                result.push(tasks.get(i));
            }
        }
        return result;
    }

}
