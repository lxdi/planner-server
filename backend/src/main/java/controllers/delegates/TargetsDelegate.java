package controllers.delegates;

import model.dao.IRealmDAO;
import model.dao.ITargetsDAO;
import model.dto.target.TargetDtoLazy;
import model.dto.target.TargetsDtoMapper;
import model.entities.Realm;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TargetsDelegate {

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    TargetsDtoMapper targetsDtoMapper;

    @Autowired
    IRealmDAO realmDAO;

    public List<TargetDtoLazy> getAllTargets(){
        List<TargetDtoLazy> result = new ArrayList<>();
        targetsDAO.allTargets().forEach(t -> result.add(targetsDtoMapper.mapToDto(t)));
        return result;
    }

    public TargetDtoLazy createTarget(TargetDtoLazy targetDto) {
        if(!(targetDto.getId()==0 && targetDto.getNextid()==null && targetDto.getRealmid()>0)){
            throw new RuntimeException("Not valid Target Dto received to create");
        }
        Realm realm = realmDAO.realmById(targetDto.getRealmid());
        Target target = targetsDtoMapper.mapToEntity(targetDto);
        Target prevTarget = targetsDAO.getLastOfChildren(target.getParent(), realm);
        targetsDAO.saveOrUpdate(target);
        TargetDtoLazy dtoLazy = targetsDtoMapper.mapToDto(target);
        if(prevTarget!=null){
            prevTarget.setNext(target);
            targetsDAO.saveOrUpdate(prevTarget);
            dtoLazy.setPrevid(prevTarget.getId());
        }
        return dtoLazy;
    }

    public void delete(long id){
        targetsDAO.deleteTarget(id);
    }

    public TargetDtoLazy update(TargetDtoLazy targetDto) {
        Target target = targetsDtoMapper.mapToEntity(targetDto);
        targetsDAO.saveOrUpdate(target);
        return targetsDtoMapper.mapToDto(target);
    }


    public List<TargetDtoLazy> updateList(List<TargetDtoLazy> targetDtoLazies){
        List<Target> updated = new ArrayList<>();
        for(TargetDtoLazy targetDtoLazy : targetDtoLazies) {
            Target target = targetsDtoMapper.mapToEntity(targetDtoLazy);
            targetsDAO.saveOrUpdate(target);
            updated.add(target);
        }
        List<TargetDtoLazy> result = new ArrayList<>();
        for(Target target: updated) {
            result.add(targetsDtoMapper.mapToDto(target));
        }
        return result;
    }


}
