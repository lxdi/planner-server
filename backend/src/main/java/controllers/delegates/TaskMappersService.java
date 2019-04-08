package controllers.delegates;

import model.SortUtils;
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
        Week week = weekDAO.getById(weekid);
        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(dayOfWeekShort);
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
            mapperExclusionDAO.save(mapperExclusion);
        }
        return mapperExclusion;
    }

    public void createTaskMappers(Layer layerToMap, Slot slot){
        if(layerToMap!=null){
            List<Task> tasks = tasksDAO.tasksByLayer(layerToMap);
            sortUtils.sortTasks(tasks);
            int fullWeekMappingUntil = tasks.size()-OPTIMAL_TASKS_AMOUNT;
            if(tasks.size()>0) {
                Stack<Task> taskStack = tasksInStack(tasks);
                List<Week> weeks = weekDAO.weeksOfHquarter(slot.getHquarter());
                List<SlotPosition> slotPositions = slotDAO.getSlotPositionsForSlot(slot);
                sortUtils.sortSlotPositions(slotPositions);
                List<MapperExclusion> exclusions = mapperExclusionDAO.getByWeeksBySPs(weeks, slotPositions);
                Task currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                validateMapping(tasks, slotPositions, weeks, exclusions);
                for(int iw = 0; iw<weeks.size(); iw++){
                    int SPsToMapAmount = slotPositions.size()-ifMappingNotOnFullWeek(iw, fullWeekMappingUntil);
                    for(int isp = 0; isp<SPsToMapAmount; isp++){
                        if(checkExclusions(exclusions, weeks.get(iw), slotPositions.get(isp))){
                            fillTaskMapperForTask(currentTask, weeks.get(iw), slotPositions.get(isp));
                            currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                            if(currentTask==null){
                                //exit all loops and complete
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

    private void validateMapping(List<Task> tasks, List<SlotPosition> SPs, List<Week> weeks, List<MapperExclusion> exclusions){
        if((SPs.size()*weeks.size()-exclusions.size())<tasks.size()){
            throw new UnsupportedOperationException("There is not enough place to schedule the all Tasks");
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

    private int ifMappingNotOnFullWeek(int weekNumFromZero, int atLeastTo){
        if(weekNumFromZero+1<=atLeastTo){
            return 0;
        } else {
            return 1;
        }
    }

}
