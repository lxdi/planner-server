package com.sogoodlabs.planner.controllers.delegates;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LayersDelegate {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    IMeansDAO meansDAO;

    public Map<String, Object> createLayer(Map<String, Object> layerDto){
        //Layer layer = layerDAO.create(meansDAO.meanById(meanid));
        Layer layer = commonMapper.mapToEntity(layerDto, new Layer());
        layerDAO.saveOrUpdate(layer);
        return commonMapper.mapToDto(layer);
    }

    public List<Map<String, Object>> createLayers(List<Map<String, Object>> layersDto){
        List<Map<String, Object>> result = new ArrayList<>();
        for(Map<String, Object> layerDto : layersDto){
            Layer layer = commonMapper.mapToEntity(layerDto, new Layer());
            layerDAO.saveOrUpdate(layer);
            result.add(commonMapper.mapToDto(layer));
        }
        //TODO return all layers of a mean, not only created ones
        return  result;
    }

    public List<Map<String, Object>> layersOfMean(long meanid){
        List<Map<String, Object>> result = new ArrayList<>();
        for(Layer layer : layerDAO.getLyersOfMean(meansDAO.meanById(meanid))){
            result.add(commonMapper.mapToDto(layer));
        }
        return result;
    }

}
