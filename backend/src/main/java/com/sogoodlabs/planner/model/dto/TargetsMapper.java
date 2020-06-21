package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.dao.ITargetsDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Slot;
import com.sogoodlabs.planner.model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.DateUtils;

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
        throw new UnsupportedOperationException();
//        Map<String, Object> result = commonMapper.mapToDto(target, new HashMap<>());
//        result.put(MEANS_COUNT, 0);
//        result.put(LAYERS, 0);
//        result.put(LAYERS_ASSIGNED, 0);
//        result.put(FINISH_DATE, null);
//
//        if(targetsDAO.isLeafTarget(target)){
//            List<Mean> meansAssigned = meansDAO.meansAssignedToTarget(target);
//            if(meansAssigned.size()>0){
//                result.put(MEANS_COUNT, meansAssigned.size());
//                List<Layer> layers = layerDAO.getLyersOfMeans(meansAssigned);
//                if(layers.size()>0){
//                    result.put(LAYERS, layers.size());
//                    List<Slot> slots = slotDAO.slotsWithLayers(layers);
//                    if(slots.size()>0){
//                        result.put(LAYERS_ASSIGNED, slots.size());
//                        if(layers.size()==slots.size()){
//                            Date date = null;
//                            for(Slot slot : slots){
//                                if(date == null || slot.getHquarter().getEndWeek().getEndDay().after(date)){
//                                    date = slot.getHquarter().getEndWeek().getEndDay();
//                                }
//                            }
//                            result.put(FINISH_DATE, DateUtils.fromDate(date));
//                        }
//                    }
//                }
//            }
//        }
//        return result;
    }

}
