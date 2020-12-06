package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.IEntity;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeansService {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

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
        return modifyMean(mean);
    }

    public Mean updateMean(Mean mean){
        return modifyMean(mean);
    }

    private Mean modifyMean(Mean mean){

        if(!isUUID(mean.getId())) {
            mean.setId(UUID.randomUUID().toString());

            Mean lastMean = mean.getParent()==null? meansDAO.getLastOfChildrenRoot(mean.getRealm()):
                    meansDAO.getLastOfChildren(mean.getParent(), mean.getRealm());

            meansDAO.save(mean);

            if(lastMean!=null){
                lastMean.setNext(mean);
                meansDAO.save(lastMean);
            }
        } else {
            meansDAO.save(mean);
        }

        if(mean.getLayers()!=null && !mean.getLayers().isEmpty()){
            for(Layer layer : mean.getLayers()){
                modifyLayer(layer, mean);
            }
        }

        return mean;
    }

    private Layer modifyLayer(Layer layer, Mean mean){
        if(!isUUID(layer.getId())){
            layer.setId(UUID.randomUUID().toString());
        }

        layer.setMean(mean);
        layerDAO.save(layer);

        if(layer.getTasks()!=null && !layer.getTasks().isEmpty()){
            Set<String> ids = layer.getTasks().stream()
                    .map(task -> modifyTask(task, layer))
                    .map(Task::getId)
                    .collect(Collectors.toSet());

            gracefulDeleteService.deleteTasksForLayerExcept(layer, ids);
        }

        return layer;
    }

    private Task modifyTask(Task task, Layer layer){
        if(!isUUID(task.getId())){
            task.setId(UUID.randomUUID().toString());
        }

        task.setLayer(layer);
        tasksDAO.save(task);

        if(task.getTopics()!=null && !task.getTopics().isEmpty()){
            task.getTopics().forEach(topic -> modifyTopic(topic, task));
        }

        if(task.getTaskTestings()!=null && !task.getTaskTestings().isEmpty()){
            task.getTaskTestings().forEach(testing -> modifyTesting(testing, task));
        }

        return task;
    }

    private Topic modifyTopic(Topic topic, Task task){
        if(!isUUID(topic.getId())){
            topic.setId(UUID.randomUUID().toString());
        }

        topic.setTask(task);
        topicDAO.save(topic);
        return topic;
    }

    private TaskTesting modifyTesting(TaskTesting testing, Task task){
        if(!isUUID(testing.getId())){
            testing.setId(UUID.randomUUID().toString());
        }

        testing.setTask(task);
        taskTestingDAO.save(testing);
        return testing;
    }

    private boolean isUUID(String id){
        return id!=null && id.matches(UUID_PATTERN);
    }

}
