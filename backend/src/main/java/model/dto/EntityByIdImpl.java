package model.dto;

import com.sogoodlabs.common_mapper.IEntityById;
import model.dao.IRepDAO;
import model.dao.IRepPlanDAO;
import model.dao.ITaskTestingDAO;
import model.dao.ITasksDAO;
import model.entities.RepetitionPlan;
import model.entities.Task;
import model.entities.TaskTesting;
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

    @Override
    public Object get(long id, Class aClass) {
        if(aClass == RepetitionPlan.class){
            return repPlanDAO.getById(id);
        }
        if(aClass == Task.class){
            return tasksDAO.getById(id);
        }
        if(aClass == TaskTesting.class){
            return testingDAO.findOne(id);
        }
        throw new NullPointerException("Unknown class in EntityByID - " + aClass.getName());
    }
}
