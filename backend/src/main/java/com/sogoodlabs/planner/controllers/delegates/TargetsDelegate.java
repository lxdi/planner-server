package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ITargetsDAO;
import com.sogoodlabs.planner.model.dto.BasicDtoValidator;
import com.sogoodlabs.planner.model.dto.TargetsMapper;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Target;
import com.sogoodlabs.planner.services.GracefulDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.util.JsonParsingFixUtils;

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

    @Autowired
    TargetsMapper targetsMapper;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;

    public List<Map<String, Object>> getAllTargets(){
        List<Map<String, Object>> result = new ArrayList<>();
        targetsDAO.findAll().forEach(t -> result.add(targetsMapper.mapToDto(t)));
        return result;
    }

    public Map<String, Object> createTarget(Map<String, Object> targetDto) {
        if(basicDtoValidator.checkIdPresence(targetDto) || !basicDtoValidator.checkForRealm(targetDto)){
            throw new RuntimeException("Not valid Target Dto received to create");
        }
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        Target prevTarget = targetsDAO.getLastOfChildren(target.getParent(), target.getRealm());
        targetsDAO.save(target);
        reassignMeansFromParent(target);
        Map<String, Object> resultDto = targetsMapper.mapToDto(target);
        if(prevTarget!=null){
            prevTarget.setNext(target);
            targetsDAO.save(prevTarget);
            resultDto.put("previd", prevTarget.getId());
        }
        return resultDto;
    }

    public void delete(long id){
        gracefulDeleteService.deleteTarget(id);
    }

    public Map<String, Object> update(Map<String, Object> targetDto) {
        if(!basicDtoValidator.checkIdPresence(targetDto) || !basicDtoValidator.checkForRealm(targetDto)){
            throw new RuntimeException("Not valid Target Dto received to update");
        }
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        targetsDAO.save(target);
        return targetsMapper.mapToDto(target);
    }


    public List<Map<String, Object>> updateList(List<Map<String, Object>> targetDtoLazies){
        List<Target> updated = new ArrayList<>();
        for(Map<String, Object> targetDtoLazy : targetDtoLazies) {
            //Target target = commonMapper.mapToEntity(targetDtoLazy, new Target());
            Target target = commonMapper.mapToEntity(targetDtoLazy,
                    targetsDAO.getOne(JsonParsingFixUtils.returnLong(targetDtoLazy.get("id"))));
            targetsDAO.save(target);
            updated.add(target);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for(Target target: updated) {
            result.add(targetsMapper.mapToDto(target));
        }
        return result;
    }

    private void reassignMeansFromParent(Target target){
        if(target.getParent()!=null){
            List<Mean> means = meansDAO.meansAssignedToTarget(target.getParent());
            if(means.size()>0){
                for(Mean mean : means){
                    List<Target> newTargets = new ArrayList<>();
                    newTargets.add(target);
                    mean.getTargets().forEach(curTar->{
                        if(curTar.getId()!=target.getParent().getId())newTargets.add(curTar);
                    });
                    mean.setTargets(newTargets);
                    meansDAO.save(mean);
                }
            }
        }
    }


}
