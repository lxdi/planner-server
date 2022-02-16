package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerOrderDAO;
import com.sogoodlabs.planner.model.entities.LayerOrder;
import com.sogoodlabs.planner.services.LayerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/layer-orders")
public class LayerOrderController {

    @Autowired
    private ILayerOrderDAO layerOrderDAO;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private LayerOrderService layerOrderService;

    @GetMapping("/all")
    public List<Map<String, Object>> getAll(){
        return layerOrderDAO.findAll().stream().map(commonMapper::mapToDto).collect(Collectors.toList());
    }

    @PutMapping(params = "layer-id")
    public Map<String, Object> createOfLayer(@RequestParam("layer-id") String layerId){
        return commonMapper.mapToDto(layerOrderService.addNewFromLayer(layerId));
    }

    @PostMapping
    public Map<String, Object> update(@RequestBody Map<String, Object> layerOrderDto){
        var layer = commonMapper.mapToEntity(layerOrderDto, new LayerOrder());
        layerOrderDAO.save(layer);
        return commonMapper.mapToDto(layer);
    }

    @PostMapping("/list")
    public List<Map<String, Object>> updateList(List<Map<String, Object>> list){
        var layerOrders = list.stream().map(dto -> commonMapper.mapToEntity(dto, new LayerOrder())).collect(Collectors.toList());
        layerOrderDAO.saveAll(layerOrders);
        return list;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id){
        var layerOrder = layerOrderDAO.findById(id).orElseThrow(()->new RuntimeException("LayerOrder doesn't exist " + id));
        layerOrderDAO.delete(layerOrder);
    }

}
