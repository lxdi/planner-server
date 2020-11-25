package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/layer")
public class LayersRESTController {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private IMeansDAO meansDAO;

    @GetMapping("/get/bymean/{meanid}")
    public List<Map<String, Object>> layersOfMean(@PathVariable("meanid") String meanid){
        Mean mean = meansDAO.findById(meanid).orElseThrow(() -> new RuntimeException("Mean not found by " + meanid));

        return layerDAO.findByMean(mean).stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
