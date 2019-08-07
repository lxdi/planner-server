package model.dto;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ILayerDAO;
import model.dao.IMeansDAO;
import model.dao.ISlotDAO;
import model.dao.ITargetsDAO;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Slot;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.DateUtils;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TargetsMapper {

    private static final String MEANS_COUNT = "meansCount";
    private static final String LAYERS = "layersCount";
    private static final String LAYERS_ASSIGNED = "layersAssignedCount";
    private static final String FINISH_DATE = "finishDate";

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISlotDAO slotDAO;

    public Map<String, Object> mapToDto(Target target){
        Map<String, Object> result = commonMapper.mapToDto(target, new HashMap<>());
        result.put(MEANS_COUNT, 0);
        result.put(LAYERS, 0);
        result.put(LAYERS_ASSIGNED, 0);
        result.put(FINISH_DATE, null);

        if(targetsDAO.isLeafTarget(target)){
            List<Mean> meansAssigned = meansDAO.meansAssignedToTarget(target);
            if(meansAssigned.size()>0){
                result.put(MEANS_COUNT, meansAssigned.size());
                List<Layer> layers = layerDAO.getLyersOfMeans(meansAssigned);
                if(layers.size()>0){
                    result.put(LAYERS, layers.size());
                    List<Slot> slots = slotDAO.slotsWithLayers(layers);
                    if(slots.size()>0){
                        result.put(LAYERS_ASSIGNED, slots.size());
                        if(layers.size()==slots.size()){
                            Date date = null;
                            for(Slot slot : slots){
                                if(date == null || slot.getHquarter().getEndWeek().getEndDay().after(date)){
                                    date = slot.getHquarter().getEndWeek().getEndDay();
                                }
                            }
                            result.put(FINISH_DATE, DateUtils.fromDate(date));
                        }
                    }
                }
            }
        }
        return result;
    }

}
