package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SafeDeleteService {

    @Autowired
    private ITargetsDAO targetsDAO;

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ISubjectDAO subjectDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITopicDAO topicDAO;

    @Autowired
    private ISpacedRepDAO spacedRepDAO;

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    public void deleteTarget(long id){
        deleteTarget(targetsDAO.getOne(id));
    }

    public void deleteTarget(Target targetToDelete) {
        //deleteDependedMeans(targetToDelete);
        unassignMeans(targetToDelete);
        handlePrevForDeleting(targetToDelete);

        for(Target target : targetsDAO.getChildren(targetToDelete)){
            this.deleteTarget(target.getId());
        }
        targetsDAO.delete(targetToDelete);
    }

    private void unassignMeans(Target target) {
        meansDAO.meansAssignedToTarget(target).forEach(mean -> {
            mean.getTargets().removeIf(curTarget -> curTarget.getId() == target.getId());
            meansDAO.save(mean);
        });
    }

    private void handlePrevForDeleting(Target target){
        Target prevTarget = targetsDAO.getPrevTarget(target);
        if(prevTarget!=null ){
            if(target.getNext()!=null){
                prevTarget.setNext(target.getNext());
            } else {
                prevTarget.setNext(null);
            }
            targetsDAO.save(prevTarget);
        }
    }

    public void deleteMean(long id) {
        deleteMean(meansDAO.getOne(id));
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
        layerDAO.getLyersOfMean(mean).forEach(this::deleteLayer);
        meansDAO.delete(mean);
    }

    public void deleteLayer(long id) {
        deleteLayer(layerDAO.layerById(id));
    }

    public void deleteLayer(Layer layer){
        subjectDAO.subjectsByLayer(layer).forEach(this::deleteSubject);
        layerDAO.delete(layer);
    }

    public void deleteSubject(long id) {
        deleteSubject(subjectDAO.getOne(id));
    }

    public void deleteSubject(Subject subject){
        tasksDAO.tasksBySubject(subject).forEach(this::deleteTask);
        subjectDAO.delete(subject);
    }

    public void deleteTask(long id) {
        deleteTask(tasksDAO.getById(id));
    }

    public void deleteTask(Task task){
        long id = task.getId();

        topicDAO.getByTaskId(id).forEach(topicDAO::delete);
        taskTestingDAO.getByTaskId(id).forEach(teting -> taskTestingDAO.delete(teting));

        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(id);
        if(spacedRepetitions!=null){
            repDAO.getRepsbySpacedRepId(spacedRepetitions.getId()).forEach(repDAO::delete);
            spacedRepDAO.remove(spacedRepetitions);
        }

        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        if(taskMapper!=null){
            taskMappersDAO.delete(taskMapper);
        }

        tasksDAO.delete(task);
    }

}
