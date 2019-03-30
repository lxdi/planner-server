package model.dto;

import com.sogoodlabs.common_mapper.IEntityById;
import model.dao.*;
import model.entities.*;
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

    @Override
    public Object get(long id, Class aClass) {
        if(aClass == Realm.class){
            return realmDAO.realmById(id);
        }
        if(aClass == Target.class){
            return targetsDAO.targetById(id);
        }
        if(aClass == Mean.class){
            return meansDAO.meanById(id);
        }
        if(aClass == Layer.class){
            return layerDAO.layerById(id);
        }
        if(aClass == Subject.class){
            return subjectDAO.getById(id);
        }
        if(aClass == Task.class){
            return tasksDAO.getById(id);
        }
        if(aClass == Topic.class){
            return topicDAO.getById(id);
        }
        if(aClass == RepetitionPlan.class){
            return repPlanDAO.getById(id);
        }
        if(aClass == TaskTesting.class){
            return testingDAO.findOne(id);
        }
        if(aClass == Repetition.class){
            return repDAO.findOne(id);
        }
        if(aClass == Week.class){
            return weekDAO.getById(id);
        }
        if(aClass == HQuarter.class){
            return hquarterDao.getById(id);
        }

        throw new NullPointerException("Unknown class in EntityByID - " + aClass.getName());
    }
}
