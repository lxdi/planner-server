package model.dao;

import model.entities.Realm;
import model.entities.Target;

import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */
public interface ITargetsDAO {

    List<Target> allTargets();
    List<Target> topTargets();
    Target targetById(long id);
    void saveOrUpdate(Target target);
    void deleteTarget(long id);
    List<Target> getChildren(Target target);
    Target getTargetByTitle(String title);
    Target getPrevTarget(Target target);
    Target getLastOfChildren(Target targetParent, Realm realm);

}
