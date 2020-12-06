package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class GracefulDeleteService {

    @Autowired
    private ITargetsDAO targetsDAO;

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
    private IRepDAO repDAO;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;


    public void deleteTarget(String id){
        deleteTarget(targetsDAO.getOne(id));
    }

    public void deleteTarget(Target targetToDelete) {
        unassignMeans(targetToDelete);
        handlePrevForDeleting(targetToDelete);

        for(Target target : targetsDAO.getChildren(targetToDelete)){
            this.deleteTarget(target.getId());
        }
        targetsDAO.delete(targetToDelete);
    }

    private void unassignMeans(Target target) {
        meansDAO.meansAssignedToTarget(target).forEach(mean -> {
            mean.getTargets().removeIf(curTarget -> curTarget.getId().equals(target.getId()));
            meansDAO.save(mean);
        });
    }

    private void handlePrevForDeleting(Target target){
        Target prevTarget = targetsDAO.getPrevTarget(target);

        if(prevTarget!=null ){
            prevTarget.setNext(target.getNext());
            targetsDAO.save(prevTarget);
        }
    }

    public void deleteMean(String id) {
        deleteMean(meansDAO.findById(id).orElseThrow(() -> new RuntimeException("Mean not found by " + id)));
    }

    public void deleteMean(Mean mean){
        Mean prevMean = meansDAO.getPrevMean(mean);
        if(prevMean!=null){
            if(mean.getNext()!=null){
                prevMean.setNext(mean.getNext());
            } else {
                prevMean.setNext(null);
            }
            meansDAO.save(prevMean);
        }
        meansDAO.getChildren(mean).forEach(this::deleteMean);
        layerDAO.findByMean(mean).forEach(this::deleteLayer);
        meansDAO.delete(mean);
    }

    public void deleteLayer(String id) {
        deleteLayer(layerDAO.findById(id).orElseThrow(() -> new RuntimeException("No Layer found by " + id)));
    }

    public void deleteLayer(Layer layer){
        tasksDAO.findByLayer(layer).forEach(this::deleteTask);
        layerDAO.delete(layer);
    }

    public void deleteTask(String id) {
        deleteTask(tasksDAO.findById(id).orElseThrow(() -> new RuntimeException("Target not found by "+ id)));
    }

    public void deleteTask(Task task){
        topicDAO.findByTask(task).forEach(topicDAO::delete);
        taskTestingDAO.findByTask(task).forEach(teting -> taskTestingDAO.delete(teting));
        taskMappersDAO.findByTask(task).forEach(taskMapper -> taskMappersDAO.delete(taskMapper));
        repDAO.findByTask(task).forEach(repDAO::delete);

        tasksDAO.delete(task);
    }

    public void deleteTasksForLayerExcept(Layer layer, Set<String> tasks){
        tasksDAO.findByLayer(layer).stream()
                .filter(task -> !tasks.contains(task.getId()))
                .forEach(this::deleteTask);
    }

}
