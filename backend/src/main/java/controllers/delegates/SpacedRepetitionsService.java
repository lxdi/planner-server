package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.IHQuarterDAO;
import model.dao.IRepDAO;
import model.dao.ITaskMappersDAO;
import model.dao.IWeekDAO;
import model.dto.TasksDtoMapper;
import model.dto.additional_mapping.AdditionalTasksMapping;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpacedRepetitionsService {

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    public Map<Integer, List<Map<String, Object>>> getActualTaskToRepeat(){
        Map<Integer, List<Map<String, Object>>> result = new HashMap<>();

        Date fromDate = DateUtils.addDays(DateUtils.currentDate(), -3);
        Date toDate = DateUtils.addDays(DateUtils.currentDate(), +3);

        result.putIfAbsent(100, new ArrayList<>());

        getCurrentTasks().forEach(task->result.get(100).add(getTaskDto(task, null)));

        List<Repetition> reps = repDAO.getUnFinishedWithPlanDateInRange(DateUtils.addWeeks(fromDate, -2), DateUtils.addWeeks(toDate, 1));

        setTasks(fromDate, toDate, 0, result, reps);
        setTasks(DateUtils.addWeeks(fromDate, 1), DateUtils.addWeeks(toDate, 1), 1, result, reps);
        setTasks(DateUtils.addWeeks(fromDate, -1), DateUtils.addWeeks(toDate, -1), -1, result, reps);
        setTasks(DateUtils.addWeeks(fromDate, -2), DateUtils.addWeeks(toDate, -2), -2, result, reps);

        return result;
    }

    private void setTasks(Date from, Date to, int weeknum, Map<Integer, List<Map<String, Object>>> result, List<Repetition> repetitions){
        result.putIfAbsent(weeknum, new ArrayList<>());
        repetitions.forEach(rep -> {
            if(rep.getPlanDate().compareTo(from)>=0 && rep.getPlanDate().compareTo(to)<=0){
                Task task = rep.getSpacedRepetitions().getTaskMapper().getTask();
                result.get(weeknum).add(getTaskDto(task, rep));
            }
        });

    }

    private Map<String, Object> getTaskDto(Task task, Repetition repetition){
        Map<String, Object> taskDto = tasksDtoMapper.mapToDtoFull(task);
        if(repetition!=null){
            taskDto.put("repetition", commonMapper.mapToDto(repetition, new HashMap<>()));
        }
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

    private List<Task> getOutDatedTasksFromCurrentHq(){
        HQuarter currentHq = ihQuarterDAO.getByDate(DateUtils.currentDate());
        if(currentHq!=null){
            //TODO
        }
        return new ArrayList<>();
    }



}
