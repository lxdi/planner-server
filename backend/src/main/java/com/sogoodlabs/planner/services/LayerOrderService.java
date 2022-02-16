package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.controllers.dto.LayerOrderAggregation;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.ILayerOrderDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.LayerOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LayerOrderService {

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ILayerOrderDAO layerOrderDAO;

    @Autowired
    private IMeansDAO meansDAO;

    public LayerOrder addNewFromLayer(String layerId){
        Layer layer = layerDAO.findById(layerId).orElseThrow(()->new RuntimeException("Layer not found " + layerId));

        if (layerOrderDAO.findByLayer(layer) !=null){
            throw new RuntimeException("Layer order already exists; layer ID " + layerId);
        }

        int order = layerOrderDAO.getMaxOrder() + 1;

        LayerOrder layerOrder = new LayerOrder();
        layerOrder.setId(UUID.randomUUID().toString());
        layerOrder.setLayerOrder(order);
        layerOrder.setLayer(layer);

        layerOrderDAO.save(layerOrder);

        return layerOrder;

    }

}
