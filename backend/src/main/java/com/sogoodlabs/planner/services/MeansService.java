package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.IEntity;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public Mean createMean(Mean mean){
        return modifyMean(mean, true);
    }

    public Mean updateMean(Mean mean){
        return modifyMean(mean, false);
    }

    private Mean modifyMean(Mean mean, boolean isCreate){

        if(isCreate) {
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

                if(isCreate){
                    modifyLayer(layer, mean, isCreate);
                    continue;
                }

                if(layer.getId()!=null && layer.getId().matches(UUID_PATTERN)){
                    modifyLayer(layer, mean, false);
                } else {
                    modifyLayer(layer, mean, true);
                }
            }
        }

        return mean;
    }

    private Layer modifyLayer(Layer layer, Mean mean, boolean isCreate){
        if(isCreate){
            layer.setId(UUID.randomUUID().toString());
        }

        layer.setMean(mean);
        layerDAO.save(layer);

        if(layer.getTasks()!=null && !layer.getTasks().isEmpty()){
            for(Task task : layer.getTasks()){

                if(isCreate){
                    modifyTask(task, layer, isCreate);
                    continue;
                }

                if(layer.getId()!=null && layer.getId().matches(UUID_PATTERN)){
                    modifyTask(task, layer, false);
                } else {
                    modifyTask(task, layer, true);
                }

            }
        }

        return layer;
    }

    private Task modifyTask(Task task, Layer layer, boolean isCreate){
        if(isCreate){
            task.setId(UUID.randomUUID().toString());
        }

        task.setLayer(layer);
        tasksDAO.save(task);

        if(task.getTopics()!=null && !task.getTopics().isEmpty()){
            for(Topic topic : task.getTopics()){

                if(isCreate){
                    modifyTopic(topic, task, isCreate);
                    continue;
                }

                if(topic.getId()!=null && topic.getId().matches(UUID_PATTERN)){
                    modifyTopic(topic, task, false);
                } else {
                    modifyTopic(topic, task, true);
                }

            }
        }

        if(task.getTaskTestings()!=null && !task.getTaskTestings().isEmpty()){
            for(TaskTesting testing : task.getTaskTestings()){

                if(isCreate){
                    modifyTesting(testing, task, isCreate);
                    continue;
                }

                if(testing.getId()!=null && testing.getId().matches(UUID_PATTERN)){
                    modifyTesting(testing, task, false);
                } else {
                    modifyTesting(testing, task, true);
                }

            }
        }


        return task;
    }

    private Topic modifyTopic(Topic topic, Task task, boolean isCreate){
        if(isCreate){
            topic.setId(UUID.randomUUID().toString());
        }

        topic.setTask(task);
        topicDAO.save(topic);
        return topic;
    }

    private TaskTesting modifyTesting(TaskTesting testing, Task task, boolean isCreate){
        if(isCreate){
            testing.setId(UUID.randomUUID().toString());
        }

        testing.setTask(task);
        taskTestingDAO.save(testing);
        return testing;
    }

    interface TripleConsumer {
        void accept(Object obj1, Object obj2, Boolean mod);
    }

    //TODO
    private void genericModification(IEntity obj, IEntity parent, boolean isCreate, TripleConsumer modFunction){

        if(isCreate){
            modFunction.accept(obj, parent, isCreate);
            return;
        }

        if(obj.getId()!=null && obj.getId().matches(UUID_PATTERN)){
            modFunction.accept(obj, parent, false);
        } else {
            modFunction.accept(obj, parent, true);
        }
    }

}
