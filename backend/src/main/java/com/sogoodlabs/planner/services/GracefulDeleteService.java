package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Autowired
    private ISlotDAO slotDAO;

    public void deleteTarget(long id){
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
        slotDAO.saveAll(slotDAO.findByMean(mean).stream()
                .peek(slot -> slot.setMean(null))
                .collect(Collectors.toList()));
        meansDAO.delete(mean);
    }

    public void deleteLayer(long id) {
        deleteLayer(layerDAO.layerById(id));
    }

    public void deleteLayer(Layer layer){
        slotDAO.saveAll(slotDAO.findByLayer(layer).stream()
                .peek(slot -> slot.setLayer(null))
                .collect(Collectors.toList()));

        subjectDAO.subjectsByLayer(layer).forEach(this::deleteSubject);
        layerDAO.delete(layer);
    }

    public void deleteSubject(long id) {
        deleteSubject(subjectDAO.getOne(id));
    }

    public void deleteSubject(Subject subject){
        tasksDAO.findBySubject(subject).forEach(this::deleteTask);
        subjectDAO.delete(subject);
    }

    public void deleteTask(long id) {
        deleteTask(tasksDAO.getOne(id));
    }

    public void deleteTask(Task task){
        long id = task.getId();

        topicDAO.getByTaskId(id).forEach(topicDAO::delete);
        taskTestingDAO.getByTaskId(id).forEach(teting -> taskTestingDAO.delete(teting));

        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(id);
        if(spacedRepetitions!=null){
            repDAO.getRepsbySpacedRepId(spacedRepetitions.getId()).forEach(repDAO::delete);
            spacedRepDAO.delete(spacedRepetitions);
        }

        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        if(taskMapper!=null){
            taskMappersDAO.delete(taskMapper);
        }

        tasksDAO.delete(task);
    }

    /**
     *  Removes all the unfinished repetitions related to the task
     *
     */
    public void removeRepetitionsLeftForTask(long taskid){
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(taskid);
        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepetitions.getId());
        repetitions.forEach(rep -> {
            if(rep.getFactDate()==null){
                repDAO.delete(rep);
            }
        });
    }

}