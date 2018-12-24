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

    public MeanDtoLazy mapToDto(Mean mean){
        MeanDtoLazy meanDtoLazy = new MeanDtoLazy();
        mapStatic(mean, meanDtoLazy);
        meanDtoLazy.setParentid(mean.getParent()!=null? mean.getParent().getId(): null);
        for(Target target : mean.getTargets()){
            meanDtoLazy.getTargetsIds().add(target.getId());
        }
        meanDtoLazy.setRealmid(mean.getRealm().getId());
        if(mean.getNext()!=null){
            meanDtoLazy.setNextid(mean.getNext().getId());
        }

        if(mean.getId()>0){
            Mean prevMean = meansDAO.getPrevMean(mean);
            if(prevMean!=null){
                meanDtoLazy.setPrevid(prevMean.getId());
            }
        }
        return meanDtoLazy;
    }

    private void mapStatic(Mean mean, MeanDtoLazy abstractMeanDto){
        abstractMeanDto.setId(mean.getId());
        abstractMeanDto.setTitle(mean.getTitle());
    }

    public Mean mapToEntity(MeanDtoLazy meanDto){
        Realm realm;
        if(meanDto.getRealmid()!=null){
            realm = realmDAO.realmById(meanDto.getRealmid());
            if(realm==null){
                throw new RuntimeException("Realm doesn't exist with id = " + meanDto.getRealmid());
            }
        } else {
            throw new RuntimeException("No realm in mean");
        }

        Mean mean = new Mean(meanDto.getTitle(), realm);
        mean.setId(meanDto.getId());
        mean.setCriteria(meanDto.getCriteria());
        if(meanDto.getParentid()!=null)
            mean.setParent(meansDAO.meanById(meanDto.getParentid()));

        if(meanDto.getNextid()!=null){
            mean.setNext(meansDAO.meanById(meanDto.getNextid()));
        }

        //TODO optimize in one call
        for(Long id : meanDto.getTargetsIds()){
            mean.getTargets().add(targetsDAO.targetById(id));
        }
        return mean;
    }

}
