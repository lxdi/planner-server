package controllers.delegates;

import model.dao.IRepPlanDAO;
import model.dto.task.TaskDtoLazy;
import model.entities.RepetitionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.DateUtils;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpacedRepetitionsService {

    @Autowired
    IRepPlanDAO repPlanDAO;

    public Map<Integer, List<TaskDtoLazy>> getActualTaskToRepeat(){
//        List<RepetitionPlan> repPlans = repPlanDAO.getAll();
//        for(RepetitionPlan repetitionPlan : repPlans){
//            for(int weeksRep : repetitionPlan.getPlan()){
//                Date repetitionDate =
//            }
//        }
        //TODO

        Date fromDate = DateUtils.currentDate();
        Date toDate = DateUtils.addWeeks(DateUtils.currentDate(), 1);

        return null;
    }

}
