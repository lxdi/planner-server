package model.dto.mean;

import model.IMeansDAO;
import model.ITargetsDAO;
import model.entities.Mean;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
public class MeansDtoMapper {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ITargetsDAO targetsDAO;

    public MeanDtoLazy meanToDtoLazy(Mean mean){
        MeanDtoLazy meanDtoLazy = new MeanDtoLazy();
        mapStatic(mean, meanDtoLazy);
        meanDtoLazy.setParentid(mean.getParent()!=null? mean.getParent().getId(): null);
        for(Target target : mean.getTargets()){
            meanDtoLazy.getTargetsIds().add(target.getId());
        }
        return meanDtoLazy;
    }

    private void mapStatic(Mean mean, AbstractMeanDto abstractMeanDto){
        abstractMeanDto.setId(mean.getId());
        abstractMeanDto.setTitle(mean.getTitle());
    }

    public Mean meanFromDto(MeanDtoLazy meanDto){
        Mean mean = new Mean();
        mapStaticFromDto(mean, meanDto);
        if(meanDto.getParentid()!=null)
            mean.setParent(meansDAO.meanById(meanDto.getParentid()));

        //TODO optimize in one call
        for(Long id : meanDto.getTargetsIds()){
            mean.getTargets().add(targetsDAO.targetById(id));
        }
        return mean;
    }

    private void mapStaticFromDto(Mean mean, AbstractMeanDto abstractMeanDto){
        mean.setId(abstractMeanDto.getId());
        mean.setTitle(abstractMeanDto.getTitle());
        mean.setCriteria(abstractMeanDto.getCriteria());
    }

}
