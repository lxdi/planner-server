package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;

import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */
public interface IMeansDAO {

    List<Mean> getAllMeans();
    Mean meanById(long id);
    void saveOrUpdate(Mean mean);
    void deleteMean(long id);
    List<Mean> getChildren(Mean mean);
    Mean meanByTitle(String title);
    //void assignQuarter(HQuarter HQuarter, Mean mean, Integer position);
    void validateMean(Mean mean);
    Mean getPrevMean(Mean mean);
    Mean getLastOfChildren(Mean mean, Realm realm);
    long assignsMeansCount(Mean mean);
    List<Mean> meansAssignedToTarget(Target target);



}
