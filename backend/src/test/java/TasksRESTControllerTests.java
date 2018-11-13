import controllers.TasksRESTController;
import model.entities.Task;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.ATestsWithTargetsMeansWeeksTasks;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by Alexander on 23.04.2018.
 */
public class TasksRESTControllerTests extends ATestsWithTargetsMeansWeeksTasks {

    MockMvc mockMvc;
    TasksRESTController tasksRESTController;

    @Before
    public void init(){
        super.init();
        tasksRESTController = new TasksRESTController(tasksDAO, tasksDtoMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(tasksRESTController).build();
    }

    @Test
    public void getAllTasksTest() throws Exception {
        MvcResult result =  mockMvc.perform(get("/task/all")).andExpect(status().isOk()).andReturn();
        String resultStr = result.getResponse().getContentAsString();
        assertTrue(resultStr.contains(task1title));
        assertTrue(resultStr.contains(task2title));
        assertTrue(resultStr.contains(task3title));
        assertTrue(resultStr.contains(task4title));
    }

    @Test
    public void createNewTaskTest() throws Exception {
        String taskTitle = "new task";
        String newTask = "{\"id\":0, \"title\":\""+taskTitle+"\", \"meanid\":1, \"weekid\": 76}";

        MvcResult result =  mockMvc.perform(put("/task/create")
                .contentType(MediaType.APPLICATION_JSON).content(newTask))
                .andExpect(status().isOk()).andReturn();

        Task savedTask = tasksDAO.byTitle(taskTitle);
        assertTrue(savedTask!=null);
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/task/delete/1")).andExpect(status().isOk());
        assertTrue(tasksDAO.byTitle(task1title)==null);
    }

    @Test
    public void updateTest() throws Exception {
        String taskTitle = "updated 1 task";
        String updatedTask = "{\"id\":1, \"title\":\""+taskTitle+"\", \"meanid\":1, \"weekid\": 76}";

        MvcResult result =  mockMvc.perform(post("/task/update")
                .contentType(MediaType.APPLICATION_JSON).content(updatedTask))
                .andExpect(status().isOk()).andReturn();
        assertTrue(tasksDAO.byTitle(task1title)==null);
        assertTrue(tasksDAO.byTitle(taskTitle).getId()==1);
    }


}
