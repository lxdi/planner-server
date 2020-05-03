package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dto.additional_mapping.AdditionalMeansMapping;
import com.sogoodlabs.planner.model.entities.Mean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MeansMapper {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    AdditionalMeansMapping additionalMeansMapping;

    public Map<String, Object> mapToDtoLazy(Mean mean){
        Map<String, Object> dto = commonMapper.mapToDto(mean);
        additionalMeansMapping.mapTargetsIdsToDto(mean, dto);
        return dto;
    }

    public Map<String, Object> mapToDtoFull(Mean mean){
        return mapToDtoFull(mean, new HashMap<>());
    }

    public Map<String, Object> mapToDtoFull(Mean mean, Map<String, Object> dto){
        Map<String, Object> result = commonMapper.mapToDto(mean, dto);
        additionalMeansMapping.mapTargetsIdsToDto(mean, result);
        additionalMeansMapping.mapLayers(mean, result);
        return dto;
    }

    public Mean mapToEntity(Map<String, Object> dto){
        Mean mean = commonMapper.mapToEntity(dto, new Mean());
        additionalMeansMapping.mapTargetsIdsToEntity(mean, dto);
        return mean;
    }

}
