package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.IRepDAO;
import model.dao.ITaskMappersDAO;
import model.dao.IWeekDAO;
import model.dto.additional_mapping.AdditionalTasksMapping;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpacedRepetitionsService {

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    AdditionalTasksMapping additionalTasksMapping;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    public Map<Integer, List<Map<String, Object>>> getActualTaskToRepeat(){
        Map<Integer, List<Map<String, Object>>> result = new HashMap<>();

        Date fromDate = DateUtils.addDays(DateUtils.currentDate(), -3);
        Date toDate = DateUtils.addDays(DateUtils.currentDate(), +3);

        result.putIfAbsent(100, new ArrayList<>());

        getCurrentTasks().forEach(task->result.get(100).add(getTaskDto(task, null)));

        setTasks(fromDate, toDate, 0, result);
        setTasks(DateUtils.addWeeks(fromDate, 1), DateUtils.addWeeks(toDate, 1), 1, result);
        setTasks(DateUtils.addWeeks(fromDate, -1), DateUtils.addWeeks(toDate, -1), -1, result);
        setTasks(DateUtils.addWeeks(fromDate, -2), DateUtils.addWeeks(toDate, -2), -2, result);

        return result;
    }

    private void setTasks(Date from, Date to, int weeknum, Map<Integer, List<Map<String, Object>>> result){
        result.putIfAbsent(weeknum, new ArrayList<>());
        repDAO.getUnFinishedWithPlanDateInRange(from, to)
                .forEach((rep)->{
                    Task task = rep.getSpacedRepetitions().getTaskMapper().getTask();
                    result.get(weeknum).add(getTaskDto(task, rep));
                });
    }

    private Map<String, Object> getTaskDto(Task task, Repetition repetition){
        Map<String, Object> taskDto = commonMapper.mapToDto(task, new HashMap<>());
        if(repetition!=null){
            taskDto.put("repetition", commonMapper.mapToDto(repetition, new HashMap<>()));
        }
        additionalTasksMapping.fillTopicsInTaskDto(taskDto);
        additionalTasksMapping.fillTestingsInTaskDto(taskDto);
        additionalTasksMapping.fillFullName(taskDto, task);
        additionalTasksMapping.fillIsFinished(task, taskDto);
        return taskDto;
    }

    private List<Task> getCurrentTasks(){
        List<Task> result = new ArrayList<>();
        Week currentWeek = weekDAO.weekOfDate(DateUtils.currentDate());
        if(currentWeek==null){ //TODO make generating weeks while the system starts
            while(currentWeek==null){ //first call, when weeks may be not generated yet by calling hquarters
                currentWeek = weekDAO.weekOfDate(DateUtils.currentDate());
            }
        }
        DaysOfWeek currentDayOfWeek = DaysOfWeek.findById(DateUtils.differenceInDays(currentWeek.getStartDay(), DateUtils.currentDate()));
        List<TaskMapper> taskMappers = taskMappersDAO.byWeekAndDay(currentWeek, currentDayOfWeek);
        taskMappers.forEach(tm->result.add(tm.getTask()));
        return result;
    }



}
