package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ITargetsDAO;
import com.sogoodlabs.planner.model.dto.BasicDtoValidator;
import com.sogoodlabs.planner.model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.JsonParsingFixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TargetsDelegate {

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    BasicDtoValidator basicDtoValidator;

    public List<Map<String, Object>> getAllTargets(){
        List<Map<String, Object>> result = new ArrayList<>();
        targetsDAO.findAll().forEach(t -> result.add(commonMapper.mapToDto(t)));
        return result;
    }

    public Map<String, Object> createTarget(Map<String, Object> targetDto) {
        if(basicDtoValidator.checkIdPresence(targetDto) || !basicDtoValidator.checkForRealm(targetDto)){
            throw new RuntimeException("Not valid Target Dto received to create");
        }
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        Target lastTarget = target.getParent()==null?
                targetsDAO.findLastAmongRoots(target.getRealm())
                :targetsDAO.findLast(target.getParent(), target.getRealm());
        targetsDAO.save(target);
        if(lastTarget!=null){
            lastTarget.setNext(target);
            targetsDAO.save(lastTarget);
        }
        return commonMapper.mapToDto(target);
    }

    public void delete(long id){
        targetsDAO.deleteById(id);
    }

    public Map<String, Object> update(Map<String, Object> targetDto) {
        if(!basicDtoValidator.checkIdPresence(targetDto) || !basicDtoValidator.checkForRealm(targetDto)){
            throw new RuntimeException("Not valid Target Dto received to update");
        }
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        targetsDAO.save(target);
        return commonMapper.mapToDto(target);
    }


    public List<Map<String, Object>> updateList(List<Map<String, Object>> targetDtoLazies){
        List<Target> updated = new ArrayList<>();
        for(Map<String, Object> targetDtoLazy : targetDtoLazies) {
            //Target target = commonMapper.mapToEntity(targetDtoLazy, new Target());
            Target target = commonMapper.mapToEntity(targetDtoLazy,
                    targetsDAO.findById(JsonParsingFixUtils.returnLong(targetDtoLazy.get("id")))).get();
            targetsDAO.save(target);
            updated.add(target);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for(Target target: updated) {
            result.add(commonMapper.mapToDto(target));
        }
        return result;
    }


}
