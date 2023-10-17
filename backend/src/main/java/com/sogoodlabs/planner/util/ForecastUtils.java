package com.sogoodlabs.planner.util;

import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Week;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

public class ForecastUtils {

    public static boolean isTasksRanOut(Map<Realm, Deque<Task>> tasks) {
        var isTasksRanOut = true;

        for(var entry : tasks.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                isTasksRanOut = false;
            }
        }

        return isTasksRanOut;
    }

    public static boolean checkRepsAccommodate(Week currentWeek, int hoursPerWeek, Set<Repetition> reps) {

        var lastWeek = getMostDistantWeek(reps);
        var week = currentWeek;

        //TODO handle week == null because hasn't been generated
        while(!week.getPrev().getId().equals(lastWeek.getId())) {

            var repsTotal = repsPerWeek(week, reps);

            if (repsTotal > hoursPerWeek) {
                return false;
            }

            week = week.getNext();
        }

        return true;
    }

    private static int repsPerWeek(Week week, Set<Repetition> reps) {
        var res = 0;

        res = res + reps.stream()
                .filter(rep -> rep.getPlanDay().getWeek().getId().equals(week.getId())).toList().size();

        return res;
    }

    private static Week getMostDistantWeek(Set<Repetition> reps) {
        Repetition res = null;

        for(var rep : reps) {
            if(res == null || res.getPlanDay().getDate().before(rep.getPlanDay().getDate())) {
                res = rep;
            }
        }

        return res.getPlanDay().getWeek();

    }

}
