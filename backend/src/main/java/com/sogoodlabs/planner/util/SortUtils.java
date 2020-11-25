package com.sogoodlabs.planner.util;

import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
