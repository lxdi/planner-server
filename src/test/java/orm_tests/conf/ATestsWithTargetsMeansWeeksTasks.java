package orm_tests.conf;

import model.ITasksDAO;
import model.entities.Task;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 26.04.2018.
 */
public abstract class ATestsWithTargetsMeansWeeksTasks extends ATestsWithTargetsMeansWeeks {

    @Autowired
    protected ITasksDAO tasksDAO;

    protected String task1title = "Task 1 title";
    protected String task2title = "Task 2 title";

    @Before
    public void init(){
        super.init();

        Task task1 = new Task();
        task1.setTitle(task1title);
        task1.setMean(meansDao.meanByTitle(parentMeanTitle));
        task1.setWeek(weekDAO.weekByStartDate(4, 6, 2018));
        tasksDAO.saveOrUpdate(task1);

        Task task2 = new Task();
        task2.setTitle(task2title);
        task2.setMean(meansDao.meanByTitle(child2MeanTitle));
        task2.setWeek(weekDAO.weekByStartDate(19, 2, 2018));
        tasksDAO.saveOrUpdate(task2);

        assertTrue(task1.getId()>0);
        assertTrue(task2.getId()>0);

    }

}
