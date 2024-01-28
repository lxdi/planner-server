package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LayerService {

    Logger log = LoggerFactory.getLogger(LayerService.class);

    @Autowired
    private ILayerDAO layerDAO;

    public List<Layer> patchList(List<Layer> layers){

        var result = new ArrayList<Layer>();

        for(Layer layerFrom : layers){
            Optional.ofNullable(patch(layerFrom)).ifPresent(result::add);
        }

        return result;
    }

    public Layer patch(Layer layerFrom){
        var layerOriginal = layerDAO.findById(layerFrom.getId()).orElse(null);

        if(layerOriginal == null){
            log.warn("Layer {} hasn't been updated", layerFrom.getId());
            return null;
        }

        BeanUtils.copyPropertiesNullIgnore(layerOriginal, layerFrom);
        layerDAO.save(layerOriginal);
        return layerOriginal;
    }

}
