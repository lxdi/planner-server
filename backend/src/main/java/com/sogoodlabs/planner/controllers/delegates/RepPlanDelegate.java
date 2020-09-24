package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RepPlanDelegate {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    IRepPlanDAO repPlanDAO;

    public List<Map<String, Object>> getAll(){
        List<Map<String, Object>> result = new ArrayList<>();
        repPlanDAO.findAll().forEach((rp)->result.add(commonMapper.mapToDto(rp, new HashMap<>())));
        return result;
    }

}
