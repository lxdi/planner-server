package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
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
    CommonMapper commonMapper;

    public Map<Integer, List<Map<String, Object>>> getActualTaskToRepeat(){
        Map<Integer, List<Map<String, Object>>> result = new HashMap<>();

        Date fromDate = DateUtils.addDays(DateUtils.currentDate(), -3);
        Date toDate = DateUtils.addDays(DateUtils.currentDate(), +3);

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
                    Map<String, Object> taskDto = commonMapper.mapToDto(rep.getSpacedRepetitions().getTaskMapper().getTask(), new HashMap<>());
                    taskDto.put("repetitionid", rep.getId());
                    result.get(weeknum).add(taskDto);
                });
    }

}
