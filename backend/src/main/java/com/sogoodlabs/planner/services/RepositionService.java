package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositionService {

    Logger log = LoggerFactory.getLogger(RepositionService.class);

    @Autowired
    private IMeansDAO meansDAO;

    public void repositionMeans(List<Mean> means){
        if(means==null || means.isEmpty()){
            return;
        }

        means.forEach(meanLight -> {
            Mean meanFull = meansDAO.findById(meanLight.getId()).orElseThrow(() -> new RuntimeException("mean doesn't exist: " + meanLight.getId()));
            meanFull.setParent(meanLight.getParent());
            meanFull.setNext(meanLight.getNext());
            meansDAO.save(meanFull);

            log.info("Mean {} was repositioned: parent id: {}, next id {}", meanFull.getId(),
                    meanFull.getParent()!=null? meanFull.getParent().getId(): "<null>",
                    meanFull.getNext()!=null? meanFull.getNext().getId(): "<null>");
        });

    }

}
