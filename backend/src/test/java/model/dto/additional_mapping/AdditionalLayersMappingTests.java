package model.dto.additional_mapping;


import model.dao.ILayerDAO;
import model.dao.ISubjectDAO;
import model.entities.Layer;
import model.entities.Subject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import services.StringUtils;
import test_configs.SpringTestConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


@Transactional
public class AdditionalLayersMappingTests extends SpringTestConfig {

    @Autowired
    AdditionalLayersMapping additionalLayersMapping;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Test
    public void mapSubjectsTest(){
        Layer layer = createLayer();
        Layer layer2 = createLayer();
        Layer layer3 = createLayer();

        Subject subject1 = createSubject(layer);
        Subject subject2 = createSubject(layer);
        Subject subject3 = createSubject(layer2);

        Map<String, Object> layerDto = new HashMap<>();
        additionalLayersMapping.mapSubjects(layer, layerDto);

        Map<String, Object> layer2Dto = new HashMap<>();
        additionalLayersMapping.mapSubjects(layer2, layer2Dto);

        Map<String, Object> layer3Dto = new HashMap<>();
        additionalLayersMapping.mapSubjects(layer3, layer3Dto);

        assertTrue(((int)StringUtils.getValue(layerDto, "get('subjects').size()"))==2);
        assertTrue(((long)StringUtils.getValue(layerDto, "get('subjects').get(0).getId()"))==subject1.getId());
        assertTrue(((long)StringUtils.getValue(layerDto, "get('subjects').get(1).getId()"))==subject2.getId());

        assertTrue(((int)StringUtils.getValue(layer2Dto, "get('subjects').size()"))==1);
        assertTrue(((long)StringUtils.getValue(layer2Dto, "get('subjects').get(0).getId()"))==subject3.getId());

        assertTrue(((int)StringUtils.getValue(layer3Dto, "get('subjects').size()"))==0);

    }

    private Subject createSubject(Layer layer){
        Subject subject = new Subject();
        subject.setLayer(layer);
        subjectDAO.saveOrUpdate(subject);
        return subject;
    }

    private Layer createLayer(){
        Layer layer = new Layer();
        layerDAO.saveOrUpdate(layer);
        return layer;
    }

}
