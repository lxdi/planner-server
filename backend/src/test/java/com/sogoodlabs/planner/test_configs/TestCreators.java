package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCreators extends ATestCreators {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    @Autowired
    IRepDAO repDAO;

    @Autowired
    IMapperExclusionDAO mapperExclusionDAO;


    @Override
    public void save(Object object) {
        if(object instanceof Realm){
            realmDAO.saveOrUpdate((Realm) object);
        }
        if(object instanceof Target){
            targetsDAO.saveOrUpdate((Target) object);
        }
        if(object instanceof Mean){
            meansDAO.save((Mean) object);
        }
        if(object instanceof HQuarter){
            ihQuarterDAO.saveOrUpdate((HQuarter) object);
        }
        if(object instanceof Subject){
            subjectDAO.save((Subject) object);
        }
        if(object instanceof Task){
            tasksDAO.saveOrUpdate((Task) object);
        }
        if(object instanceof Topic){
            topicDAO.save((Topic) object);
        }
        if(object instanceof Layer){
            layerDAO.saveOrUpdate((Layer) object);
        }
        if(object instanceof TaskTesting){
            taskTestingDAO.save((TaskTesting) object);
        }
        if(object instanceof Slot){
            slotDAO.saveOrUpdate((Slot) object);
        }
        if(object instanceof SlotPosition){
            slotDAO.saveOrUpdate((SlotPosition) object);
        }
        if(object instanceof SpacedRepetitions){
            spacedRepDAO.save((SpacedRepetitions) object);
        }
        if(object instanceof Repetition){
            repDAO.save((Repetition) object);
        }
        if(object instanceof MapperExclusion){
            mapperExclusionDAO.save((MapperExclusion) object);
        }

    }
}
