package com.sogoodlabs.planner.services;


import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeanFillerService {

    private static final String PROGRESS_STATUS_SCHEDULED="scheduled";
    private static final String PROGRESS_STATUS_COMPLETED="completed";

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITopicDAO topicDAO;

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private ProgressService progressService;

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
        task.setProgressStatus(getProgressStatus(task));
        task.setProgress(progressService.getByTask(task));
    }

    private String getProgressStatus(Task task){
        if(taskMappersDAO.findByTaskFinished(task).size()>0){
            return PROGRESS_STATUS_COMPLETED;
        }
        if(taskMappersDAO.findByTaskUnfinished(task).size()>0){
            return PROGRESS_STATUS_SCHEDULED;
        }
        return null;
    }

}
