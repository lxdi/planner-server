package model.dto.mean;


import model.dao.IMeansDAO;
import model.dto.IMapper;
import model.entities.Mean;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
@Transactional
public class MeansDtoLazyMapper implements IMapper<MeanDtoLazy, Mean> {

    @Autowired
    IMeansDAO meansDAO;

    public MeanDtoLazy mapToDto(Mean mean){
        MeanDtoLazy meanDtoLazy = new MeanDtoLazy();
        mapToDto(mean, meanDtoLazy);
        return meanDtoLazy;
    }

    public void mapToDto(Mean mean, MeanDtoLazy meanDtoLazy){
        meanDtoLazy.setId(mean.getId());
        meanDtoLazy.setTitle(mean.getTitle());
        meanDtoLazy.setParentid(mean.getParent()!=null? mean.getParent().getId(): null);
        for(Target target : mean.getTargets()){
            meanDtoLazy.getTargetsIds().add(target.getId());
        }
        meanDtoLazy.setRealmid(mean.getRealm().getId());
        if(mean.getNext()!=null){
            meanDtoLazy.setNextid(mean.getNext().getId());
        }
        meanDtoLazy.setHideChildren(mean.isHideChildren());
    }

    public Mean mapToEntity(MeanDtoLazy meanDto){
        throw new UnsupportedOperationException();
    }

}
