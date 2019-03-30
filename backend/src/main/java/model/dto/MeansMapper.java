package model.dto;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dto.additional_mapping.AdditionalMeansMapping;
import model.entities.Mean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Map<String, Object> dto = commonMapper.mapToDto(mean);
        additionalMeansMapping.mapTargetsIdsToDto(mean, dto);
        additionalMeansMapping.mapLayers(mean, dto);
        return dto;
    }

    public Mean mapToEntity(Map<String, Object> dto){
        Mean mean = commonMapper.mapToEntity(dto, new Mean());
        additionalMeansMapping.mapTargetsIdsToEntity(mean, dto);
        return mean;
    }

}
