package com.sogoodlabs.planner.util;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class SortUtils {

    public static List<Task> sortTasks(List<Task> taskList) {
        taskList.sort((task1, task2) -> {
            if (task1.getPosition() > task2.getPosition()) {
                return 1;
            }
            if (task1.getPosition() < task2.getPosition()) {
                return -1;
            }
            return 0;
        });
        return taskList;
    }

    public static List<Day> sortDistantDays(List<Day> allDays){
        List<Day> result = new ArrayList<>();

        for(int i = 0, j = 0; i<allDays.size(); i++){
            if(i==0){
                result.add(allDays.get(i));
                continue;
            }

            if(allDays.get(i).getWeekDay().getId()-allDays.get(j).getWeekDay().getId()==1){
                continue;
            }

            result.add(allDays.get(i));
            j=i;
        }

        return result;
    }


}
