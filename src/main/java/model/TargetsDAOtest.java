package model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */

@Component
public class TargetsDAOtest implements ITargetsDAO {


    @Override
    public List<Target> firstLevelTargets() {
        List<Target> targets = new ArrayList<>();
        targets.add(createTarget(1, "Test target 1"));
        targets.add(createTarget(2, "Test target 2"));
        return targets;
    }

    private Target createTarget(int id, String title){
        Target target = new Target();
        target.setId(id);
        target.setTitle(title);
        return target;
    }
}
