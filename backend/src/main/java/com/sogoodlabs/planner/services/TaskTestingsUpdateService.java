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

    public static final String NEW_ID_PREFIX = "new-"; //duplicated on the frontend

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    private static class TestingMeta {
        TaskTesting testing;
        String parentid;
        String nextid;

        TestingMeta(TaskTesting testing, String parentid, String nextid){
            this.testing = testing;
            this.nextid = nextid;
            this.parentid = parentid;
        }
    }

    public void update(Task task, List<TaskTesting> testings){
        if(testings!=null){

            Map<String, TestingMeta> testingMetaMap = resolve(testings);

            Set<String> ids = testingMetaMap.entrySet().stream()
                    .filter(entry -> !entry.getKey().contains(NEW_ID_PREFIX))
                    .peek(entry->{
                        TaskTesting testing = entry.getValue().testing;
                        if(entry.getValue().parentid!=null){
                            testing.setParent(testingMetaMap.get(entry.getValue().parentid).testing);
                        }
                        if(entry.getValue().nextid!=null){
                            testing.setNext(testingMetaMap.get(entry.getValue().nextid).testing);
                        }
                        modify(testing, task);
                    })
                    .map(entry -> entry.getValue().testing.getId())
                    .collect(Collectors.toSet());

            List<TaskTesting> testingsByTask = taskTestingDAO.findByTask(task);

            testingsByTask.stream()
                    .filter(testing -> !ids.contains(testing.getId()))
                    .forEach(testing -> {
                        testing.setNext(null);
                        testing.setParent(null);
                        taskTestingDAO.save(testing);
                    });

            testingsByTask.stream()
                    .filter(testing -> !ids.contains(testing.getId()))
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

    private Map<String, TestingMeta> resolve(List<TaskTesting> testings){
        Map<String, TestingMeta> result = new HashMap<>();
        testings.forEach(testing -> {
            String parentid = testing.getParent()!=null? testing.getParent().getId(): null;
            String nextid = testing.getNext()!=null? testing.getNext().getId(): null;
            testing.setParent(null);
            testing.setNext(null);
            TestingMeta testingMeta = new TestingMeta(testing, parentid, nextid);
            if(testing.getId().contains(NEW_ID_PREFIX)){
                String oldId = testing.getId();
                result.put(oldId, testingMeta);
                testing.setId(UUID.randomUUID().toString());
            }
            result.put(testing.getId(), testingMeta);
            taskTestingDAO.save(testing);
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
