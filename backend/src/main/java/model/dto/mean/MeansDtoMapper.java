package model.dto.mean;

import model.dao.IMeansDAO;
import model.dao.IHQuarterDAO;
import model.dao.IRealmDAO;
import model.dao.ITargetsDAO;
import model.dto.IMapper;
import model.entities.Mean;
import model.entities.Realm;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
public class MeansDtoMapper implements IMapper<MeanDtoLazy, Mean> {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    IHQuarterDAO quarterDAO;

    public MeanDtoLazy mapToDto(Mean mean){
        MeanDtoLazy meanDtoLazy = new MeanDtoLazy();
        mapStatic(mean, meanDtoLazy);
        meanDtoLazy.setParentid(mean.getParent()!=null? mean.getParent().getId(): null);
        for(Target target : mean.getTargets()){
            meanDtoLazy.getTargetsIds().add(target.getId());
        }
        meanDtoLazy.setRealmid(mean.getRealm().getId());
//        if(mean.getHquarter()!=null)
//            meanDtoLazy.setQuarterid(mean.getHquarter().getId());
//        if(mean.getPosition()!=null && mean.getPosition()>0){
//            meanDtoLazy.setPosition(mean.getPosition());
//        }
        return meanDtoLazy;
    }

    private void mapStatic(Mean mean, AbstractMeanDto abstractMeanDto){
        abstractMeanDto.setId(mean.getId());
        abstractMeanDto.setTitle(mean.getTitle());
    }

    public Mean mapToEntity(MeanDtoLazy meanDto){
        Mean mean = new Mean();
        mapStaticFromDto(mean, meanDto);
        if(meanDto.getParentid()!=null)
            mean.setParent(meansDAO.meanById(meanDto.getParentid()));

        //TODO optimize in one call
        for(Long id : meanDto.getTargetsIds()){
            mean.getTargets().add(targetsDAO.targetById(id));
        }

        if(meanDto.getRealmid()!=null){
            Realm realm = realmDAO.realmById(meanDto.getRealmid());
            if(realm==null){
                throw new RuntimeException("Realm doesn't exist with id = " + meanDto.getRealmid());
            }
            mean.setRealm(realm);
        } else {
            throw new RuntimeException("No realm in mean");
        }

//        if(meanDto.getQuarterid()!=null){
//            mean.setHquarter(quarterDAO.getById(meanDto.getQuarterid()));
//        }
//        if(meanDto.getPosition()!=null){
//            mean.setPosition(meanDto.getPosition());
//        }
        return mean;
    }

    private void mapStaticFromDto(Mean mean, AbstractMeanDto abstractMeanDto){
        mean.setId(abstractMeanDto.getId());
        mean.setTitle(abstractMeanDto.getTitle());
        mean.setCriteria(abstractMeanDto.getCriteria());
    }

}
