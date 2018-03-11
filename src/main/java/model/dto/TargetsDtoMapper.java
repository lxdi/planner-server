package model.dto;

import model.ITargetsDAO;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
public class TargetsDtoMapper {

    @Autowired
    ITargetsDAO targetsDAO;

    public TargetDtoWithParentLazy targetDtoWithParentLazy(Target target){
        TargetDtoWithParentLazy targetDtoWithParentLazy = new TargetDtoWithParentLazy();
        mapStatic(target, targetDtoWithParentLazy);
        targetDtoWithParentLazy.setParentid(target.getParent()!=null? target.getParent().getId(): null);
        return targetDtoWithParentLazy;
    }

    private void mapStatic(Target target, AbstractTargetDto abstractTargetDto){
        abstractTargetDto.setId(target.getId());
        abstractTargetDto.setTitle(target.getTitle());
    }

    public Target targetFromDtoWithParentLazy(TargetDtoWithParentLazy targetDtoWithParentLazy){
        Target target = new Target();
        mapStaticFromDto(target, targetDtoWithParentLazy);
        target.setParent(targetsDAO.targetById(targetDtoWithParentLazy.getParentid()));
        return target;
    }

    private void mapStaticFromDto(Target target, AbstractTargetDto abstractTargetDto){
        target.setId(abstractTargetDto.getId());
        target.setTitle(abstractTargetDto.getTitle());
    }

}
