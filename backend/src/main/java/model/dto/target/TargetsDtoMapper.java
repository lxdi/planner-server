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
        mapStatic(target, targetDtoLazy);
        targetDtoLazy.setParentid(target.getParent()!=null? target.getParent().getId(): null);
        targetDtoLazy.setRealmid(target.getRealm().getId());
        return targetDtoLazy;
    }

    private void mapStatic(Target target, AbstractTargetDto abstractTargetDto){
        abstractTargetDto.setId(target.getId());
        abstractTargetDto.setTitle(target.getTitle());
    }

    public Target mapToEntity(TargetDtoLazy targetDtoLazy) {
        Target target = new Target();
        mapStaticFromDto(target, targetDtoLazy);
        if(targetDtoLazy.getParentid()!=null)
            target.setParent(targetsDAO.targetById(targetDtoLazy.getParentid()));

        if(targetDtoLazy.getRealmid()!=null){
            target.setRealm(realmDTO.realmById(targetDtoLazy.getRealmid()));
        } else {
            throw new RuntimeException("realmid is empty");
        }

        return target;
    }

    private void mapStaticFromDto(Target target, AbstractTargetDto abstractTargetDto){
        target.setId(abstractTargetDto.getId());
        target.setTitle(abstractTargetDto.getTitle());
    }

}
