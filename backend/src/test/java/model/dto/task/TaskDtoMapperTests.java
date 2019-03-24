package model.dto.task;

import model.dao.ITaskTestingDAO;
import model.dao.ITasksDAO;
import model.entities.Task;
import model.entities.TaskTesting;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test_configs.SpringTestConfig;

import static org.junit.Assert.assertTrue;

public class TaskDtoMapperTests extends SpringTestConfig {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Test
    public void mapToDtoTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskTesting taskTesting1 = new TaskTesting();
        taskTesting1.setQuestion("test question");
        taskTesting1.setTask(task);
        taskTestingDAO.save(taskTesting1);

        TaskDtoLazy result = tasksDtoMapper.mapToDto(task);

        assertTrue(result.getTestings().size()==1);
        assertTrue((long)result.getTestings().get(0).get("id")==taskTesting1.getId());
        assertTrue(result.getTestings().get(0).get("question").equals("test question"));

    }

}
