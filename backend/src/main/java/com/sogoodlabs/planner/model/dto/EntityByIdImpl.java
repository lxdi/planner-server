package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.common_mapper.IEntityById;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.services.TaskTestingsUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityByIdImpl implements IEntityById<String> {

    @Autowired
    IRepPlanDAO repPlanDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskTestingDAO testingDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    IRepDAO repDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private IExternalTaskDao externalTaskDao;

    @Override
    public Object get(String id, Class aClass) {
        if(id==null || aClass == null){
            return null;
        }
        Object result = null;
        if(aClass == Realm.class){
            result = realmDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == Mean.class){
            result = meansDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == Layer.class){
            result = layerDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == Task.class){
            result = tasksDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == Topic.class){
            result = topicDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == RepetitionPlan.class){
            result = repPlanDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }

        if(aClass == TaskTesting.class){
            result = testingDAO.findById(id).orElseGet(()-> {
                if(id.contains(TaskTestingsUpdateService.NEW_ID_PREFIX)){
                    TaskTesting taskTesting = new TaskTesting();
                    taskTesting.setId(id);
                    return taskTesting;
                }
                throw new RuntimeException(aClass.getName() + " not found by " + id);
            });
        }

        if(aClass == Repetition.class){
            result = repDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == Week.class){
            result = weekDAO.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == Day.class){
            result = dayDao.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }
        if(aClass == ExternalTask.class){
            result = externalTaskDao.findById(id).orElseThrow(()-> new RuntimeException(aClass.getName() + " not found by " + id));
        }

        if(result ==null){
            throw new NullPointerException("Entity not found; class: "+aClass.getName()+"; id: "+id);
        }

        return result;
    }
}
