package model;

import model.entities.Target;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */

@Component
public class TargetsDAOtest implements ITargetsDAO {

    List<Target> firstLevelTargets;

    @Override
    public List<Target> firstLevelTargets() {
        if(this.firstLevelTargets==null){
            firstLevelTargets = new ArrayList<>();
            firstLevelTargets.add(createTarget(1, "Test target 1"));
            firstLevelTargets.add(createTarget(2, "Test target 2"));
        }
        return this.firstLevelTargets;
    }

    @Override
    public Target targetById(long id) {
        for(Target target: firstLevelTargets){
            if(target.getId()==id)
                return target;
        }
        return null;
    }

    private Target createTarget(int id, String title){
        Target target = new Target();
        target.setId(id);
        target.setTitle(title);
        return target;
    }
}
