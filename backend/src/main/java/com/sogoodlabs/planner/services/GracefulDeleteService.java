package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@Transactional
public class GracefulDeleteService {

    Logger log = LoggerFactory.getLogger(GracefulDeleteService.class);

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

        log.info("deleting target {}", targetToDelete.getId());

        unassignMeans(targetToDelete);
        handlePrevForDeleting(targetToDelete);

        for(Target target : targetsDAO.getChildren(targetToDelete)){
            this.deleteTarget(target.getId());
        }
        targetsDAO.delete(targetToDelete);
    }

    private void unassignMeans(Target target) {
        meansDAO.meansAssignedToTarget(target).forEach(mean -> {
            log.info("unassign mean {} from target {}", mean.getId(), target.getId());
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
        delete(meansDAO.findById(id).orElseThrow(() -> new RuntimeException("Mean not found by " + id)));
    }

    public void delete(Mean mean){
        Mean prevMean = meansDAO.getPrevMean(mean);
        if(prevMean!=null){
            if(mean.getNext()!=null){
                prevMean.setNext(mean.getNext());
            } else {
                prevMean.setNext(null);
            }
            meansDAO.save(prevMean);
        }
        meansDAO.getChildren(mean).forEach(this::delete);
        layerDAO.findByMean(mean).forEach(this::delete);
        meansDAO.delete(mean);
    }

    public void deleteLayer(String id) {
        delete(layerDAO.findById(id).orElseThrow(() -> new RuntimeException("No Layer found by " + id)));
    }

    public void delete(Layer layer){
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
