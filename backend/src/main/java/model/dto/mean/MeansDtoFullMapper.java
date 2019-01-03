package model.dto.mean;

import model.dao.ILayerDAO;
import model.dao.IMeansDAO;
import model.dao.IRealmDAO;
import model.dao.ITargetsDAO;
import model.dto.IMapper;
import model.dto.layer.LayersDtoMapper;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Realm;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
public class MeansDtoFullMapper implements IMapper<MeanDtoFull, Mean> {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    MeansDtoLazyMapper meansDtoLazyMapper;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    LayersDtoMapper layersDtoMapper;

    public MeanDtoFull mapToDto(Mean mean){
        MeanDtoFull meanDtoLazy = new MeanDtoFull();
        meansDtoLazyMapper.mapToDto(mean, meanDtoLazy);
        meanDtoLazy.setCriteria(mean.getCriteria());
        List<Layer> layers = layerDAO.getLyersOfMean(mean);
        if(layers.size()>0){
            for(Layer layer : layers){
                meanDtoLazy.getLayers().add(layersDtoMapper.mapToDto(layer));
            }
        }
        return meanDtoLazy;
    }

    public Mean mapToEntity(MeanDtoFull meanDto){
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
