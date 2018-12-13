package orm_tests;

import model.dao.ITaskMappersDAO;
import model.dao.ITasksDAO;
import model.entities.Task;
import model.entities.TaskMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.AbstractTestsWithTargets;

import static junit.framework.TestCase.assertTrue;

public class TaskMapperDaoTests extends AbstractTestsWithTargets {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Before
    public void init(){
        super.init();
    }

    @Test
    public void taskMapperByTaskTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        assertTrue(taskMappersDAO.taskMapperForTask(task).getId()==taskMapper.getId());
    }
}
