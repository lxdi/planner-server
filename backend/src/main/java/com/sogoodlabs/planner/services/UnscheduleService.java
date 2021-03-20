package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ITaskMappersDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnscheduleService {

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    public void unscheduleMean(String meanId){
        Mean mean = meansDAO.findById(meanId).orElseThrow(()-> new RuntimeException("Mean not found " + meanId));
        layerDAO.findByMean(mean).forEach(layer -> this.unscheduleLayer(layer.getId()));
    }

    public void unscheduleLayer(String layerId){
        Layer layer = layerDAO.findById(layerId).orElseThrow(()->new RuntimeException("Layer not found " + layerId));
        tasksDAO.findByLayer(layer).forEach(task -> unscheduleTask(task.getId()));
    }

    public void unscheduleTask(String taskId){
        Task task = tasksDAO.findById(taskId).orElseThrow(()->new RuntimeException("Task not found " + taskId));
        taskMappersDAO.findByTaskUnfinisihed(task).forEach(taskMappersDAO::delete);
    }

}
