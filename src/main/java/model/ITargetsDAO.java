package model;

import model.entities.Target;

import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */
public interface ITargetsDAO {
    List<Target> firstLevelTargets();
    Target targetById(long id);

}
