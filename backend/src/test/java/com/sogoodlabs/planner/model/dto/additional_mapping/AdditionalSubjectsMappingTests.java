package com.sogoodlabs.planner.model.dto.additional_mapping;

import com.sogoodlabs.planner.model.dao.ISubjectDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
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
public class AdditionalSubjectsMappingTests extends SpringTestConfig {

    @Autowired
    AdditionalSubjectsMapping additionalSubjectsMapping;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Test
    public void mapSubjectsTest(){

        Subject subject1 = createSubject();
        Task task11 = createTask(subject1);
        Task task12 = createTask(subject1);

        Subject subject2 = createSubject();
        Task task21 = createTask(subject2);

        Subject subject3 = createSubject();

        Map<String, Object> subject1Dto = new HashMap<>();
        additionalSubjectsMapping.mapTasks(subject1, subject1Dto);

        Map<String, Object> subject2Dto = new HashMap<>();
        additionalSubjectsMapping.mapTasks(subject2, subject2Dto);

        Map<String, Object> subject3Dto = new HashMap<>();
        additionalSubjectsMapping.mapTasks(subject3, subject3Dto);

        assertTrue(((int)StringUtils.getValue(subject1Dto, "get('tasks').size()"))==2);
        assertTrue(((long)StringUtils.getValue(subject1Dto, "get('tasks').get(0).get('id')"))==task11.getId());
        assertTrue(((long)StringUtils.getValue(subject1Dto, "get('tasks').get(1).get('id')"))==task12.getId());

        assertTrue(((int)StringUtils.getValue(subject2Dto, "get('tasks').size()"))==1);
        assertTrue(((long)StringUtils.getValue(subject2Dto, "get('tasks').get(0).get('id')"))==task21.getId());

        assertTrue(((int)StringUtils.getValue(subject3Dto, "get('tasks').size()"))==0);

    }

    private Subject createSubject(){
        Subject subject = new Subject();
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
