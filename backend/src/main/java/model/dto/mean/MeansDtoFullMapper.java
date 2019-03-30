package model.dto.mean;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ILayerDAO;
import model.dao.IMeansDAO;
import model.dao.IRealmDAO;
import model.dao.ITargetsDAO;
import model.dto.IMapper;
import model.dto.additional_mapping.AdditionalLayersMapping;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 10.03.2018.
 */

@Component
@Transactional
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
    CommonMapper commonMapper;

    @Autowired
    AdditionalLayersMapping additionalLayersMapping;

    public MeanDtoFull mapToDto(Mean mean){
        MeanDtoFull meanDtoLazy = new MeanDtoFull();
        meansDtoLazyMapper.mapToDto(mean, meanDtoLazy);
        meanDtoLazy.setCriteria(mean.getCriteria());
        List<Layer> layers = layerDAO.getLyersOfMean(mean);
        if(layers.size()>0){
            for(Layer layer : layers){
                Map<String, Object> layerDto = commonMapper.mapToDto(layer);
                additionalLayersMapping.mapSubjects(layer, layerDto);
                meanDtoLazy.getLayers().add(commonMapper.mapToDto(layer));
            }
        }
        Mean prevMean = meansDAO.getPrevMean(mean);
        if(prevMean!=null){
            meanDtoLazy.setPrevid(prevMean.getId());
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
        mean.setHideChildren(meanDto.isHideChildren());
        return mean;
    }

}
