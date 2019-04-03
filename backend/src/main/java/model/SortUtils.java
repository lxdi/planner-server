package model;

import model.entities.SlotPosition;
import model.entities.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SortUtils {

    public void sortTasks(List<Task> taskList){
        taskList.sort((task1, task2) -> {
            if(task1.getSubject().getPosition()>task2.getSubject().getPosition()){
                return 1;
            }
            if(task1.getSubject().getPosition()<task2.getSubject().getPosition()){
                return -1;
            }
            if(task1.getSubject().getPosition()==task2.getSubject().getPosition()){
                if(task1.getPosition()>task2.getPosition()){
                    return 1;
                }
                if(task1.getPosition()<task2.getPosition()){
                    return -1;
                }
            }
            return 0;
        });
    }

    public void sortSlotPositions(List<SlotPosition> slotPositionList){
        slotPositionList.sort((sp1, sp2) -> {
            if(sp1.getDayOfWeek().getId()>sp2.getDayOfWeek().getId()){
                return 1;
            }
            if(sp1.getDayOfWeek().getId()<sp2.getDayOfWeek().getId()){
                return -1;
            }
            if(sp1.getDayOfWeek().getId()==sp2.getDayOfWeek().getId()){
                if(sp1.getPosition()>sp2.getPosition()){
                    return 1;
                }
                if(sp1.getPosition()<sp2.getPosition()){
                    return -1;
                }
            }
            return 0;
        });
    }

}
