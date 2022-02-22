package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.services.LayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/layers")
public class LayersRESTController {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private LayerService layerService;

    @GetMapping
    public List<Map<String, Object>> layersOfMean(@RequestParam("mean-id") String meanid){
        Mean mean = meansDAO.findById(meanid).orElseThrow(() -> new RuntimeException("Mean not found by " + meanid));

        return layerDAO.findByMean(mean).stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PutMapping
    public Map<String, Object> create(@RequestBody  Map<String, Object> layerDto){
        Layer layer = commonMapper.mapToEntity(layerDto, new Layer());
        layer.setId(UUID.randomUUID().toString());
        layerDAO.save(layer);
        return commonMapper.mapToDto(layer);
    }

    @PatchMapping
    public Map<String, Object> patch(@RequestBody Map<String, Object> dto){
        var layer = commonMapper.mapToEntity(dto, new Layer());
        return commonMapper.mapToDto(layerService.patch(layer));
    }

    @PatchMapping("/list")
    public List<Map<String, Object>> patchList(@RequestBody List<Map<String, Object>> dtoList){
        var layers = dtoList.stream().map(dto -> commonMapper.mapToEntity(dto, new Layer())).collect(Collectors.toList());
        return layerService.patchList(layers).stream().map(commonMapper::mapToDto).collect(Collectors.toList());
    }

}
