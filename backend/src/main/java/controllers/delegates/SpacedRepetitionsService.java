package controllers.delegates;

import model.dao.IRepDAO;
import model.dto.task.TaskDtoLazy;
import model.dto.task.TasksDtoMapper;
import model.entities.Repetition;
import model.entities.Task;
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
    TasksDtoMapper tasksDtoMapper;

    public Map<Integer, List<TaskDtoLazy>> getActualTaskToRepeat(){
        Map<Integer, List<TaskDtoLazy>> result = new HashMap<>();

        Date fromDate = DateUtils.addDays(DateUtils.currentDate(), -3);
        Date toDate = DateUtils.addDays(DateUtils.currentDate(), +3);

        setTasks(fromDate, toDate, 0, result);
        setTasks(DateUtils.addWeeks(fromDate, 1), DateUtils.addWeeks(toDate, 1), 1, result);
        setTasks(DateUtils.addWeeks(fromDate, -1), DateUtils.addWeeks(toDate, -1), -1, result);
        setTasks(DateUtils.addWeeks(fromDate, -2), DateUtils.addWeeks(toDate, -2), -2, result);

        return result;
    }

    private void setTasks(Date from, Date to, int weeknum, Map<Integer, List<TaskDtoLazy>> result){
        List<Task> repeatRightNow = new ArrayList<>();
        repDAO.getUnFinishedWithPlanDateInRange(from, to)
                .forEach((rep)->repeatRightNow.add(rep.getSpacedRepetitions().getTaskMapper().getTask()));
        result.putIfAbsent(weeknum, new ArrayList<>());
        repeatRightNow.forEach((task)->result.get(weeknum).add(tasksDtoMapper.mapToDto(task)));
    }

}
