package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ITargetsDAO;
import model.dto.BasicDtoValidator;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        targetsDAO.allTargets().forEach(t -> result.add(commonMapper.mapToDto(t)));
        return result;
    }

    public Map<String, Object> createTarget(Map<String, Object> targetDto) {
        if(basicDtoValidator.checkIdPresence(targetDto) || !basicDtoValidator.checkForRealm(targetDto)){
            throw new RuntimeException("Not valid Target Dto received to create");
        }
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        Target prevTarget = targetsDAO.getLastOfChildren(target.getParent(), target.getRealm());
        targetsDAO.saveOrUpdate(target);
        Map<String, Object> resultDto = commonMapper.mapToDto(target);
        if(prevTarget!=null){
            prevTarget.setNext(target);
            targetsDAO.saveOrUpdate(prevTarget);
            resultDto.put("previd", prevTarget.getId());
        }
        return resultDto;
    }

    public void delete(long id){
        targetsDAO.deleteTarget(id);
    }

    public Map<String, Object> update(Map<String, Object> targetDto) {
        if(!basicDtoValidator.checkIdPresence(targetDto) || !basicDtoValidator.checkForRealm(targetDto)){
            throw new RuntimeException("Not valid Target Dto received to update");
        }
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        targetsDAO.saveOrUpdate(target);
        return commonMapper.mapToDto(target);
    }


    public List<Map<String, Object>> updateList(List<Map<String, Object>> targetDtoLazies){
        List<Target> updated = new ArrayList<>();
        for(Map<String, Object> targetDtoLazy : targetDtoLazies) {
            Target target = commonMapper.mapToEntity(targetDtoLazy, new Target());
            targetsDAO.saveOrUpdate(target);
            updated.add(target);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for(Target target: updated) {
            result.add(commonMapper.mapToDto(target));
        }
        return result;
    }


}
