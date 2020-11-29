package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MeansService {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    public Mean createMean(Mean mean){
        return modifyMean(mean, true);
    }

    public Mean updateMean(Mean mean){
        return modifyMean(mean, false);
    }

    private Mean modifyMean(Mean mean, boolean isCreate){

        if(isCreate) {
            mean.setId(UUID.randomUUID().toString());

            Mean lastMean = mean.getParent()==null? meansDAO.getLastOfChildrenRoot(mean.getRealm()):
                    meansDAO.getLastOfChildren(mean.getParent(), mean.getRealm());

            meansDAO.save(mean);

            if(lastMean!=null){
                lastMean.setNext(mean);
                meansDAO.save(lastMean);
            }
        } else {
            meansDAO.save(mean);
        }

        if(mean.getLayers()!=null && !mean.getLayers().isEmpty()){
            for(Layer layer : mean.getLayers()){

                if(isCreate){
                    modifyLayer(layer, mean, isCreate);
                    continue;
                }

                if(layer.getId()!=null && layer.getId().matches(UUID_PATTERN)){
                    modifyLayer(layer, mean, false);
                } else {
                    modifyLayer(layer, mean, true);
                }
            }
        }

        return mean;
    }

    private Layer modifyLayer(Layer layer, Mean mean, boolean isCreate){
        if(isCreate){
            layer.setId(UUID.randomUUID().toString());
        }

        layer.setMean(mean);
        layerDAO.save(layer);

        if(layer.getTasks()!=null && !layer.getTasks().isEmpty()){
            for(Task task : layer.getTasks()){

                if(isCreate){
                    modifyTask(task, layer, isCreate);
                    continue;
                }

                if(layer.getId()!=null && layer.getId().matches(UUID_PATTERN)){
                    modifyTask(task, layer, false);
                } else {
                    modifyTask(task, layer, true);
                }

            }
        }

        return layer;
    }

    private Task modifyTask(Task task, Layer layer, boolean isCreate){
        if(isCreate){
            task.setId(UUID.randomUUID().toString());
        }

        task.setLayer(layer);
        tasksDAO.save(task);
        return task;
    }

}
