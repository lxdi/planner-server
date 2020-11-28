package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MeansService {

    @Autowired
    private IMeansDAO meansDAO;

    public Mean createMean(Mean mean){
        mean.setId(UUID.randomUUID().toString());

        Mean lastMean = mean.getParent()==null? meansDAO.getLastOfChildrenRoot(mean.getRealm()):
                meansDAO.getLastOfChildren(mean.getParent(), mean.getRealm());

        meansDAO.save(mean);

        if(lastMean!=null){
            lastMean.setNext(mean);
            meansDAO.save(lastMean);
        }

        return mean;
    }
}
