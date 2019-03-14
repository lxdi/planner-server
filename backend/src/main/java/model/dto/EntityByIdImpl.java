package model.dto;

import com.sogoodlabs.common_mapper.IEntityById;
import model.dao.IRepDAO;
import model.dao.IRepPlanDAO;
import model.entities.RepetitionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityByIdImpl implements IEntityById {

    @Autowired
    IRepPlanDAO repPlanDAO;

    @Override
    public Object get(long id, Class aClass) {
        if(aClass == RepetitionPlan.class){
            return repPlanDAO.getById(id);
        }
        return null;
    }
}
