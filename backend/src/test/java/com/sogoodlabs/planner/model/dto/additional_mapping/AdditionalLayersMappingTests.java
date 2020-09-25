package com.sogoodlabs.planner.model.dto.additional_mapping;


import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.ISubjectDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Subject;
import com.sogoodlabs.planner.model.entities.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.StringUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;

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

    @Autowired
    ITasksDAO tasksDAO;

    @Test
    public void mapSubjectsTest(){
        Layer layer = createLayer();
        Layer layer2 = createLayer();
        Layer layer3 = createLayer();

        Subject subject1 = createSubject(layer);
        Task task11 = createTask(subject1);
        Task task12 = createTask(subject1);

        Subject subject2 = createSubject(layer);
        Task task21 = createTask(subject2);

        Subject subject3 = createSubject(layer2);

        Map<String, Object> layerDto = new HashMap<>();
        additionalLayersMapping.mapSubjects(layer, layerDto);

        Map<String, Object> layer2Dto = new HashMap<>();
        additionalLayersMapping.mapSubjects(layer2, layer2Dto);

        Map<String, Object> layer3Dto = new HashMap<>();
        additionalLayersMapping.mapSubjects(layer3, layer3Dto);

        assertTrue(((int)StringUtils.getValue(layerDto, "get('subjects').size()"))==2);

        assertTrue(((long)StringUtils.getValue(layerDto, "get('subjects').get(0).get('id')"))==subject1.getId());
        assertTrue(((int)StringUtils.getValue(layerDto, "get('subjects').get(0).get('tasks').size()"))==2);
        assertTrue(((long)StringUtils.getValue(layerDto, "get('subjects').get(0).get('tasks').get(0).get('id')"))==task11.getId());
        assertTrue(((long)StringUtils.getValue(layerDto, "get('subjects').get(0).get('tasks').get(1).get('id')"))==task12.getId());

        assertTrue(((long)StringUtils.getValue(layerDto, "get('subjects').get(1).get('id')"))==subject2.getId());

        assertTrue(((int)StringUtils.getValue(layer2Dto, "get('subjects').size()"))==1);
        assertTrue(((long)StringUtils.getValue(layer2Dto, "get('subjects').get(0).get('id')"))==subject3.getId());

        assertTrue(((int)StringUtils.getValue(layer3Dto, "get('subjects').size()"))==0);

    }

    private Layer createLayer(){
        Layer layer = new Layer();
        layerDAO.saveOrUpdate(layer);
        return layer;
    }

    private Subject createSubject(Layer layer){
        Subject subject = new Subject();
        subject.setLayer(layer);
        subjectDAO.save(subject);
        return subject;
    }

    private Task createTask(Subject subject){
        Task task = new Task();
        task.setSubject(subject);
        tasksDAO.save(task);
        return task;
    }

}
