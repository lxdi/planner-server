package model.dto.additional_mapping;


import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ISubjectDAO;
import model.entities.Layer;
import model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdditionalLayersMapping {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    AdditionalSubjectsMapping additionalSubjectsMapping;

    public void mapSubjects(Layer layer, Map<String, Object> result){
        result.putIfAbsent("subjects", new ArrayList<>());
        List<Subject> subjects = subjectDAO.subjectsByLayer(layer);
        if(subjects.size()>0) {
            subjects.forEach(subject -> {
                    Map<String, Object> subjdto = commonMapper.mapToDto(subject);
                    additionalSubjectsMapping.mapTasks(subject, subjdto);
                    ((ArrayList)StringUtils.getValue(result, "get('subjects')")).add(subjdto);
            });
        }
    }

}


