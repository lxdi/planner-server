package com.sogoodlabs.planner.services;


import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.ITaskTestingDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.dao.ITopicDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeanFillerService {

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITopicDAO topicDAO;

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    public void fill(Mean mean){
        List<Layer> layers = layerDAO.findByMean(mean);
        layers.forEach(this::fill);
        mean.setLayers(layers);
    }

    private void fill(Layer layer){
        List<Task> tasks = tasksDAO.findByLayer(layer);
        tasks.forEach(this::fill);
        layer.setTasks(tasks);
    }

    private void fill(Task task){
        task.setTopics(topicDAO.findByTask(task));
        task.setTaskTestings(taskTestingDAO.findByTask(task));
    }

}
