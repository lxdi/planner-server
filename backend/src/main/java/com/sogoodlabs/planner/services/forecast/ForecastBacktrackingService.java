package com.sogoodlabs.planner.services.forecast;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.services.ProgressService;
import com.sogoodlabs.planner.util.function.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sogoodlabs.planner.util.ForecastUtils.checkRepsAccommodate;
import static com.sogoodlabs.planner.util.ForecastUtils.isTasksRanOut;


@Service
public class ForecastBacktrackingService {

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private ProgressService progressService;

    public Date forecastBacktracking(Week currentWeek, int hoursTotal, int hoursOccupiedByTasks,
                                      Set<Repetition> validReps,
                                      Map<Realm, Deque<Task>> tasks,
                                      TriConsumer<Week, Task, Set<Repetition>> taskAssignedCallback) {

        if (isTasksRanOut(tasks)) {
            return null;
        }

        var repsInCurrentWeek = validReps.stream()
                .filter(rep -> rep.getPlanDay().getWeek().getId().equals(currentWeek.getId()))
                .toList();

        var hoursAvail = hoursTotal - hoursOccupiedByTasks - repsInCurrentWeek.size();

        if (hoursAvail < 2) {
            return forecastBacktracking(currentWeek.getNext(), hoursTotal, 0, validReps, tasks, taskAssignedCallback);
        }

        var sundayOfCurrentWeek = dayDao.findByWeek(currentWeek).stream()
                .filter(day -> day.getWeekDay() == DaysOfWeek.sun)
                .findFirst().get();

        Date bestDate = null;
        Task chosenTask = null;
        Set<Repetition> chosenTaskReps = null;

        for(var entry : tasks.entrySet()) {

            if(entry.getValue().size()<1) {
                continue;
            }

            var curTask = entry.getValue().pop();
            var potReps = new HashSet<Repetition>();

            if (curTask.getRepetitionPlan() != null) {
                potReps.addAll(progressService.generateRepetitions(curTask.getRepetitionPlan(), sundayOfCurrentWeek.getDate(), curTask));
                validReps.addAll(potReps);

                if (!checkRepsAccommodate(currentWeek.getNext(), hoursTotal, validReps)) {
                    entry.getValue().addFirst(curTask);
                    validReps.removeAll(potReps);
                    continue;
                }
            }

            var localRes = forecastBacktracking(currentWeek, hoursTotal, hoursOccupiedByTasks + 2, validReps, tasks, taskAssignedCallback);

            if (localRes==null) {
                localRes = sundayOfCurrentWeek.getDate();
            }

            if (bestDate == null || localRes.before(bestDate)) {
                bestDate = localRes;
                chosenTask = curTask;
                chosenTaskReps = potReps;
            }

            entry.getValue().addFirst(curTask);
            validReps.removeAll(potReps);
        }

        if (taskAssignedCallback != null) {
            taskAssignedCallback.accept(currentWeek, chosenTask, chosenTaskReps);
        }

        return bestDate;

    }


}
