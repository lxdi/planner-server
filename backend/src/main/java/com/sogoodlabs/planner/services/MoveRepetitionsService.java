package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MoveRepetitionsService {

    Logger log = LoggerFactory.getLogger(MoveRepetitionsService.class);

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private IDayDao dayDao;

    public void move(List<Repetition> reps, Day targetDay) {
        Map<Task, Integer> tasksDays = new HashMap<>();

        Set<String> repIds = reps.stream()
                .peek(repetition -> moveRepetition(repetition, targetDay, tasksDays))
                .map(Repetition::getId)
                .collect(Collectors.toSet());

        tasksDays.forEach((key, value) -> moveRepetitionsForTask(key, value, repIds));
    }

    private void moveRepetition(Repetition repetition, Day targetDay, Map<Task, Integer> tasksDays){

        if(tasksDays!=null){
            int difference = DateUtils.differenceInDays(repetition.getPlanDay().getDate(), targetDay.getDate());
            tasksDays.putIfAbsent(repetition.getTask(), 0);

            if(tasksDays.get(repetition.getTask())<Math.abs(difference)){
                tasksDays.put(repetition.getTask(), difference);
            }
        }

        repetition.setPlanDay(targetDay);
        repDAO.save(repetition);
        log.info("Moving repetition {} to {}", repetition.getId(), DateUtils.fromDate(targetDay.getDate()));
    }

    private void moveRepetitionsForTask(Task task, int dayDiff, Set<String> repsToExclude){
        repDAO.findByTaskActive(task).stream()
                .filter(rep -> !repsToExclude.contains(rep.getId()))
                .forEach(rep -> {
                    Day targetDay = dayDao.findByDate(DateUtils.addDays(rep.getPlanDay().getDate(), dayDiff));
                    moveRepetition(rep, targetDay, null);
                });

    }

}
