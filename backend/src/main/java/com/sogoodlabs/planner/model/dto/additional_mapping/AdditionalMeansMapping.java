package com.sogoodlabs.planner.model.dto.additional_mapping;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.ITargetsDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sogoodlabs.planner.services.JsonParsingFixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdditionalMeansMapping {

    private static final String TARGETSIDS_FIELD_TITLE = "targetsIds";
    private static final String LAYERS_FIELD_TITLE = "layers";

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    AdditionalLayersMapping additionalLayersMapping;

    @Autowired
    CommonMapper commonMapper;

    public void mapLayers(Mean mean, Map<String, Object> dto){
        dto.putIfAbsent(LAYERS_FIELD_TITLE, new ArrayList<>());
        layerDAO.getLyersOfMean(mean).forEach(layer -> {
            Map<String, Object> layerdto = commonMapper.mapToDto(layer);
            additionalLayersMapping.mapSubjects(layer, layerdto);
            ((List)dto.get(LAYERS_FIELD_TITLE)).add(layerdto);
        });
    }

    public void mapTargetsIdsToDto(Mean mean, Map<String, Object> dto){
        dto.putIfAbsent(TARGETSIDS_FIELD_TITLE , new ArrayList<>());
        mean.getTargets().forEach(target -> {
            ((ArrayList)dto.get(TARGETSIDS_FIELD_TITLE)).add(target.getId());
        });
    }

    public void mapTargetsIdsToEntity(Mean mean, Map<String, Object> dto){
        if(dto.get(TARGETSIDS_FIELD_TITLE )!=null){
            List<Long> targetsIds = convertToListLong((List) dto.get(TARGETSIDS_FIELD_TITLE ));
            for(Long id : targetsIds){
                Target target = targetsDAO.getOne(id);
                if(target!=null){
                    mean.getTargets().add(target);
                }
            }
        }
    }

    private List<Long> convertToListLong(List targets){
        List<Long> result = new ArrayList<>();
        if(targets.size()>0){
            targets.forEach(i->result.add(JsonParsingFixUtils.returnLong(i)));
        }
        return result;
    }

}
