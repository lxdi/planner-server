package com.sogoodlabs.planner.controllers.delegates;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.ISubjectDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Subject;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.services.SafeDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class SubjectsDelegate {

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    SafeDeleteService safeDeleteService;

    @Autowired
    CommonMapper commonMapper;

    public List<Map<String, Object>> layersOfMean(long layerid){
        List<Map<String, Object>> result = new ArrayList<>();
        for(Subject subject : subjectDAO.subjectsByLayer(layerDAO.layerById(layerid))){
            result.add(commonMapper.mapToDto(subject));
        }
        return result;
    }

    public Map<String, Object> delete(long subjectid){
        Subject subject = subjectDAO.getById(subjectid);
        if(subject!=null) {
            safeDeleteService.deleteSubject(subject);
            return commonMapper.mapToDto(subject);
        } else {
            throw new RuntimeException("Subject doesn't exist");
        }
    }

}
