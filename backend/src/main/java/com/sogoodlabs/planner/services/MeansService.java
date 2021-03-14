package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeansService {

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITopicDAO topicDAO;

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;

    public Mean createMean(Mean mean){
        return modify(mean);
    }

    public Mean updateMean(Mean mean){
        return modify(mean);
    }

    public Mean modify(Mean mean){
        save(mean);

        if(mean.getLayers()!=null && !mean.getLayers().isEmpty()){
            Set<String> ids = mean.getLayers().stream()
                    .peek(layer -> modify(layer, mean))
                    .map(Layer::getId)
                    .collect(Collectors.toSet());

            layerDAO.findByMean(mean).stream()
                    .filter(layer -> !ids.contains(layer.getId()))
                    .forEach(gracefulDeleteService::delete);
        }

        return mean;
    }

    private void save(Mean mean){
        if(IdUtils.isUUID(mean.getId())) {
            meansDAO.save(mean);
            return;
        }
        mean.setId(UUID.randomUUID().toString());

        Mean lastMean = mean.getParent() == null ? meansDAO.getLastOfChildrenRoot(mean.getRealm()) :
                meansDAO.getLastOfChildren(mean.getParent(), mean.getRealm());

        meansDAO.save(mean);

        if (lastMean != null) {
            lastMean.setNext(mean);
            meansDAO.save(lastMean);
        }
    }

    private Layer modify(Layer layer, Mean mean){
        if(!IdUtils.isUUID(layer.getId())){
            layer.setId(UUID.randomUUID().toString());
        }

        layer.setMean(mean);
        layerDAO.save(layer);

        if(layer.getTasks()!=null && !layer.getTasks().isEmpty()){
            Set<String> ids = layer.getTasks().stream()
                    .map(task -> modify(task, layer))
                    .map(Task::getId)
                    .collect(Collectors.toSet());

            gracefulDeleteService.deleteTasksForLayerExcept(layer, ids);
        }

        return layer;
    }

    private Task modify(Task task, Layer layer){
        if(!IdUtils.isUUID(task.getId())){
            task.setId(UUID.randomUUID().toString());
        }

        task.setLayer(layer);
        tasksDAO.save(task);

        if(task.getTopics()!=null && !task.getTopics().isEmpty()){

            Set<String> ids = task.getTopics().stream()
                    .peek(topic -> modify(topic, task))
                    .map(Topic::getId)
                    .collect(Collectors.toSet());

            topicDAO.findByTask(task).stream()
                    .filter(topic -> !ids.contains(topic.getId()))
                    .forEach(topicDAO::delete);
        }

        if(task.getTaskTestings()!=null && !task.getTaskTestings().isEmpty()){

            Set<String> ids = task.getTaskTestings().stream()
                    .peek(taskTesting -> modify(taskTesting, task))
                    .map(TaskTesting::getId)
                    .collect(Collectors.toSet());

            taskTestingDAO.findByTask(task).stream()
                    .filter(topic -> !ids.contains(topic.getId()))
                    .forEach(taskTestingDAO::delete);
        }

        return task;
    }

    private Topic modify(Topic topic, Task task){
        if(!IdUtils.isUUID(topic.getId())){
            topic.setId(UUID.randomUUID().toString());
        }

        topic.setTask(task);
        topicDAO.save(topic);
        return topic;
    }

    private TaskTesting modify(TaskTesting testing, Task task){
        if(!IdUtils.isUUID(testing.getId())){
            testing.setId(UUID.randomUUID().toString());
        }

        testing.setTask(task);
        taskTestingDAO.save(testing);
        return testing;
    }

}
