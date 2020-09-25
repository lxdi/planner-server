package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.common_mapper.IEntityById;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityByIdImpl implements IEntityById {

    @Autowired
    IRepPlanDAO repPlanDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskTestingDAO testingDAO;

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    IRepDAO repDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    IHQuarterDAO hquarterDao;

    @Autowired
    ISlotDAO slotDAO;

    @Override
    public Object get(long id, Class aClass) {
        if(id==0){
            return null;
        }
        Object result = null;
        if(aClass == Realm.class){
            result = realmDAO.realmById(id);
        }
        if(aClass == Target.class){
            result = targetsDAO.getOne(id);
        }
        if(aClass == Mean.class){
            result = meansDAO.getOne(id);
        }
        if(aClass == Layer.class){
            result = layerDAO.layerById(id);
        }
        if(aClass == Subject.class){
            result = subjectDAO.getOne(id);
        }
        if(aClass == Task.class){
            result = tasksDAO.getById(id);
        }
        if(aClass == Topic.class){
            result = topicDAO.getOne(id);
        }
        if(aClass == RepetitionPlan.class){
            result = repPlanDAO.getOne(id);
        }
        if(aClass == TaskTesting.class){
            result = testingDAO.getOne(id);
        }
        if(aClass == Repetition.class){
            result = repDAO.findOne(id);
        }
        if(aClass == Week.class){
            result = weekDAO.getById(id);
        }
        if(aClass == HQuarter.class){
            result = hquarterDao.getById(id);
        }
        if(aClass == Slot.class){
            result = slotDAO.getById(id);
        }
        if(aClass == SlotPosition.class){
            result = slotDAO.getSlotPositionById(id);
        }

        if(result ==null){
            throw new NullPointerException("Entity not found; class: "+aClass.getName()+"; id: "+id);
        }

        return result;
    }
}
