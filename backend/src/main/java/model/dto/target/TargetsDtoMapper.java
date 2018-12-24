package model.dto.target;

import model.dao.IRealmDAO;
import model.dao.ITargetsDAO;
import model.dto.IMapper;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
public class TargetsDtoMapper implements IMapper<TargetDtoLazy, Target> {

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IRealmDAO realmDTO;

    public TargetDtoLazy mapToDto(Target target){
        TargetDtoLazy targetDtoLazy = new TargetDtoLazy();
        targetDtoLazy.setId(target.getId());
        targetDtoLazy.setTitle(target.getTitle());
        targetDtoLazy.setParentid(target.getParent()!=null? target.getParent().getId(): null);
        targetDtoLazy.setRealmid(target.getRealm().getId());
        if(target.getNext()!=null){
            targetDtoLazy.setNextid(target.getNext().getId());
        }

        if(target.getId()>0){
            Target prevTarget= targetsDAO.getPrevTarget(target);
            if(prevTarget!=null){
                targetDtoLazy.setPrevid(prevTarget.getId());
            }
        }

        return targetDtoLazy;
    }

    public Target mapToEntity(TargetDtoLazy targetDtoLazy) {
        Target target = new Target();
        target.setId(targetDtoLazy.getId());
        target.setTitle(targetDtoLazy.getTitle());
        if(targetDtoLazy.getParentid()!=null)
            target.setParent(targetsDAO.targetById(targetDtoLazy.getParentid()));

        if(targetDtoLazy.getNextid()!=null){
            target.setNext(targetsDAO.targetById(targetDtoLazy.getNextid()));
        }

        if(targetDtoLazy.getRealmid()!=null){
            target.setRealm(realmDTO.realmById(targetDtoLazy.getRealmid()));
        } else {
            throw new RuntimeException("realmid is empty");
        }

        return target;
    }

}
