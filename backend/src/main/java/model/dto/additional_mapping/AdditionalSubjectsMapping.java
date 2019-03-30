package model.dto.additional_mapping;

import model.dao.ITasksDAO;
import model.dto.TasksMapperService;
import model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdditionalSubjectsMapping {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TasksMapperService tasksMapperService;

    public void mapTasks(Subject subject, Map<String, Object> result){
        result.putIfAbsent("tasks", new ArrayList<>());
        tasksDAO.tasksBySubject(subject).forEach(task ->
            ((List)result.get("tasks")).add(tasksMapperService.mapToDtoFull(task)));
    }

}
