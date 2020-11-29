package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Target;
import com.sogoodlabs.planner.services.GracefulDeleteService;
import com.sogoodlabs.planner.services.MeansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 23.02.2018.
 */

@RestController
@RequestMapping(path = "/mean")
public class MeansRESTController {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final String ID_DTO_FIELD = "id";
    private static final String LAYERS_DTO_FIELD = "layers";
    private static final String MEAN_ID_DTO_FIELD = "meanid";

    @Autowired
    private LayersRESTController layersRESTController;

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;

    @Autowired
    private MeansService meansService;

    @GetMapping("/get/all")
    public List<Map<String, Object>> getAllTargets(){
        return meansDAO.findAll().stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/create")
    public Map<String, Object> createTarget(@RequestBody Map<String, Object> meanDto) {
        Mean mean = meansService.createMean(commonMapper.mapToEntity(meanDto, new Mean()));

        if(meanDto.get(LAYERS_DTO_FIELD)!=null){
            for(Map<String, Object> layerDto : (List<Map<String, Object>>) meanDto.get(LAYERS_DTO_FIELD)){
                layerDto.put(MEAN_ID_DTO_FIELD, mean.getId());
                layersRESTController.create(layerDto);
            }
        }
        return commonMapper.mapToDto(mean);
    }

    @DeleteMapping("/delete/{targetId}")
    public void delete(@PathVariable("targetId") String id) {
        Mean mean = meansDAO.findById(id).orElseThrow(() -> new RuntimeException("Mean not found by " + id));
        gracefulDeleteService.deleteMean(mean);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Map<String, Object> meanDto) {
        Mean mean = updateOneMean(meanDto);

        if(meanDto.get(LAYERS_DTO_FIELD)!=null){
            for(Map<String, Object> layerDto : (List<Map<String, Object>>) meanDto.get(LAYERS_DTO_FIELD)){

                layerDto.put(MEAN_ID_DTO_FIELD, mean.getId());
                String id = (String) layerDto.get(ID_DTO_FIELD);

                if(id.matches(UUID_PATTERN)){
                    layersRESTController.update(layerDto);
                } else {
                    layersRESTController.create(layerDto);
                }
            }
        }

        return commonMapper.mapToDto(mean);
    }

    @PostMapping("/update/list")
    public List<Map<String, Object>> updateList(@RequestBody List<Map<String, Object>> meanDtoList){
        return meanDtoList.stream()
                .map(mean -> commonMapper.mapToDto(this.updateOneMean(mean)))
                .collect(Collectors.toList());
    }

    private Mean updateOneMean(Map<String, Object> meanDto){
        Mean mean = commonMapper.mapToEntity(meanDto, new Mean());
        meansDAO.save(mean);
        return mean;
    }

}
