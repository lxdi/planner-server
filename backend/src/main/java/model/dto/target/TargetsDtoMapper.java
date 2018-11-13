package model.dto.target;

import model.IRealmDAO;
import model.ITargetsDAO;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
public class TargetsDtoMapper {

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IRealmDAO realmDTO;

    public TargetDtoLazy targetDtoWithParentLazy(Target target){
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

    public Target targetFromDtoWithParentLazy(TargetDtoLazy targetDtoLazy) {
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
