package services;

import model.entities.Task;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SpringUtilsTests {

    @Test
    public void getFullNameTest(){
        Task task = new Task();
        task.setTitle("task");

        String fullName = StringUtils.getFullName(task, new String[]{
                "subject?.layer?.mean?.realm?.title", "subject?.layer?.mean?.title",
                "subject?.layer?.priority", "subject?.title", "title"
        });

        assertTrue(fullName.equals("task"));
    }

}
