package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.ITaskTestingDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.TaskTesting;
import com.sogoodlabs.planner.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskTestingsUpdateService {

    public static final String NEW_ID_PREFIX = "new-";

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    public void update(Task task, List<TaskTesting> testings){
        if(testings!=null && !testings.isEmpty()){

            Map<String, TaskTesting> testingMap = resolveNew(testings);

            Set<String> ids = testings.stream()
                    .peek(taskTesting -> {

                        if(taskTesting.getParent()!=null && taskTesting.getParent().getId().contains(NEW_ID_PREFIX)){
                            taskTesting.setParent(testingMap.get(taskTesting.getParent().getId()));
                        }
                        if(taskTesting.getNext()!=null && taskTesting.getNext().getId().contains(NEW_ID_PREFIX)){
                            taskTesting.setNext(testingMap.get(taskTesting.getNext().getId()));
                        }

                        modify(taskTesting, task);
                    })
                    .map(TaskTesting::getId)
                    .collect(Collectors.toSet());

            taskTestingDAO.findByTask(task).stream()
                    .filter(topic -> !ids.contains(topic.getId()))
                    .forEach(taskTestingDAO::delete);
        }

    }

    private TaskTesting modify(TaskTesting testing, Task task){
        if(!IdUtils.isUUID(testing.getId())){
            testing.setId(UUID.randomUUID().toString());
        }

        testing.setTask(task);
        taskTestingDAO.save(testing);
        return testing;
    }

    private Map<String, TaskTesting> resolveNew(List<TaskTesting> testings){
        Map<String, TaskTesting> result = new HashMap<>();
        testings.stream()
                .filter(testing -> testing.getId().contains(NEW_ID_PREFIX))
                .forEach(testing -> {
                    String oldId = testing.getId();
                    testing.setId(UUID.randomUUID().toString());
                    result.put(oldId, testing);
                });
        return result;
    }

    //TODO remove after migration
    public void flatAllTestings(){
        tasksDAO.findAll().forEach(task -> {
            TaskTesting current = null;
            for(TaskTesting testing : taskTestingDAO.findByTask(task)){
                testing.setParent(null);
                testing.setNext(current);
                taskTestingDAO.save(testing);
                current = testing;
            }
        });
    }

}
