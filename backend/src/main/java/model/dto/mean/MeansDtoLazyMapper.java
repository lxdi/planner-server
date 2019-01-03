package model.dto.mean;


import model.dto.IMapper;
import model.entities.Mean;
import model.entities.Target;
import org.springframework.stereotype.Component;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
public class MeansDtoLazyMapper implements IMapper<MeanDtoLazy, Mean> {

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
    }

    public Mean mapToEntity(MeanDtoLazy meanDto){
        throw new UnsupportedOperationException();
    }

}
