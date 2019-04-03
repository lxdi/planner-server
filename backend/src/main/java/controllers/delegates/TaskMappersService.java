package controllers.delegates;

import model.dao.*;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TaskMappersService {

    private static final int OPTIMAL_TASKS_AMOUNT = 8;

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
                createTaskMappers(layers.get(i), slots.get(i), null);
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

    public void rescheduleTaskMappers(long weekid, String dayOfWeekShort){
        //TODO validate pushing
        Week week = weekDAO.getById(weekid);
        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(dayOfWeekShort);
        taskMappersDAO.byWeekAndDay(week, dayOfWeek).forEach(taskMapper -> {
            Map<Long, List<Long>> exclusions = new HashMap<>();
            exclusions.put(weekid, new ArrayList<>(Arrays.asList(taskMapper.getSlotPosition().getId())));
            this.rescheduleTaskMappers(taskMapper.getSlotPosition().getSlot(), exclusions);
        });
    }

    public void rescheduleTaskMappers(Slot slot, Map<Long, List<Long>> weekidSPidsExclusions){
        if(slot!=null && slot.getLayer()!=null){
            createTaskMappers(slot.getLayer(), slot, weekidSPidsExclusions);
        }
    }

    public void createTaskMappers(Layer layerToMap, Slot slot, Map<Long, List<Long>> weekidSPidsExclusions){
        if(layerToMap!=null){
            List<Task> tasks = tasksDAO.tasksByLayer(layerToMap);
            Collections.sort(tasks);
            int fullWeekMappingUntil = tasks.size()-OPTIMAL_TASKS_AMOUNT;
            if(tasks.size()>0) {
                Stack<Task> taskStack = tasksInStack(tasks);
                List<Week> weeks = weekDAO.weeksOfHquarter(slot.getHquarter());
                List<SlotPosition> slotPositions = slotDAO.getSlotPositionsForSlot(slot);
                Collections.sort(slotPositions);
                Task currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                for(int iw = 0; iw<weeks.size(); iw++){
                    int SPsToMapAmount = slotPositions.size()-ifMappingNotOnFullWeek(iw, fullWeekMappingUntil);
                    for(int isp = 0; isp<SPsToMapAmount; isp++){
                        if(checkExclusions(weekidSPidsExclusions, weeks.get(iw), slotPositions.get(isp))){
                            fillTaskMapperForTask(currentTask, weeks.get(iw), slotPositions.get(isp));
                            currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                            if(currentTask==null){
                                iw=weeks.size();
                                isp=slotPositions.size();
                            }
                        } else {
                            if(SPsToMapAmount<slotPositions.size()){
                                SPsToMapAmount++;
                            } else {
                                fullWeekMappingUntil++;
                            }
                        }
                    }
                }
            }
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

    private boolean checkExclusions(Map<Long, List<Long>> exclusions, Week week, SlotPosition slotPosition){
        if(exclusions==null){
            return true;
        }
        List<Long> spIds = exclusions.get(week.getId());
        if(spIds!=null && spIds.contains(slotPosition.getId())){
            return false;
        }
        return true;
    }

    private int ifMappingNotOnFullWeek(int weekNumFromZero, int atLeastTo){
        if(weekNumFromZero+1<=atLeastTo){
            return 0;
        } else {
            return 1;
        }
    }

}
