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
        if(id==0){
            return null;
        }
        Object result = null;
        if(aClass == Realm.class){
            result = realmDAO.realmById(id);
        }
        if(aClass == Target.class){
            result = targetsDAO.targetById(id);
        }
        if(aClass == Mean.class){
            result = meansDAO.meanById(id);
        }
        if(aClass == Layer.class){
            result = layerDAO.layerById(id);
        }
        if(aClass == Subject.class){
            result = subjectDAO.getById(id);
        }
        if(aClass == Task.class){
            result = tasksDAO.getById(id);
        }
        if(aClass == Topic.class){
            result = topicDAO.getById(id);
        }
        if(aClass == RepetitionPlan.class){
            result = repPlanDAO.getById(id);
        }
        if(aClass == TaskTesting.class){
            result = testingDAO.findOne(id);
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

        if(result ==null){
            throw new NullPointerException("Entity not found; class: "+aClass.getName()+"; id: "+id);
        }

        return result;
    }
}
