import controllers.delegates.HquartersDelegate;
import model.dao.*;
import model.dto.hquarter.HquarterDtoFull;
import model.dto.hquarter.HquarterDtoFullMapper;
import model.dto.hquarter.WeekWithTasksDto;
import model.entities.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;

import java.util.List;
import java.util.Stack;

import static junit.framework.TestCase.assertTrue;
import static services.DateUtils.toDate;

public class HquarterDtoLazyMapper_WeeksMappingTests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    HquartersDelegate hquartersDelegate;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    HquarterDtoFullMapper hquarterDtoFullMapper;

    Mean mean;
    HQuarter hQuarter1;
    Slot slot1;
    SlotPosition slotPosition11;
    SlotPosition slotPosition12;
    SlotPosition slotPosition13;

    Slot slot2;
    SlotPosition slotPosition21;
    SlotPosition slotPosition22;
    SlotPosition slotPosition23;

    @Before
    public void init(){
        super.init();

        hQuarter1 = ihQuarterDAO.getHQuartersInYear(2018).get(0);

        //----
        slot1 = new Slot(hQuarter1, 1);
        slotDAO.saveOrUpdate(slot1);

        slotPosition11 = new SlotPosition(slot1, DaysOfWeek.mon, 1);
        slotDAO.saveOrUpdate(slotPosition11);

        slotPosition12 = new SlotPosition(slot1, DaysOfWeek.fri, 3);
        slotDAO.saveOrUpdate(slotPosition12);

        slotPosition13 = new SlotPosition(slot1, DaysOfWeek.sun, 2);
        slotDAO.saveOrUpdate(slotPosition13);

        //----
        slot2 = new Slot(hQuarter1, 1);
        slotDAO.saveOrUpdate(slot2);

        slotPosition21 = new SlotPosition(slot2, DaysOfWeek.tue, 1);
        slotDAO.saveOrUpdate(slotPosition21);

        slotPosition22 = new SlotPosition(slot2, DaysOfWeek.fri, 1);
        slotDAO.saveOrUpdate(slotPosition22);

        slotPosition23 = new SlotPosition(slot2, DaysOfWeek.fri, 2);
        slotDAO.saveOrUpdate(slotPosition23);

        //----
        mean = new Mean("test mean", realm);
        meansDAO.saveOrUpdate(mean);

        //--
        Layer layer1 = new Layer(mean, 1);
        layerDAO.saveOrUpdate(layer1);

        Subject subject1 = new Subject(layer1, 1);
        subjectDAO.saveOrUpdate(subject1);

        createTask("task 1", subject1, 1);
        createTask("task 2", subject1, 2);
        createTask("task 3", subject1, 3);

        Subject subject2 = new Subject(layer1, 2);
        subjectDAO.saveOrUpdate(subject2);

        createTask("task 5", subject2, 2);
        createTask("task 6", subject2, 3);
        createTask("task 4", subject2, 1);
        createTask("task 7", subject2, 4);

        Subject subject3 = new Subject(layer1, 3);
        subjectDAO.saveOrUpdate(subject3);

        createTask("task 9", subject3, 2);
        createTask("task 8", subject3, 1);

        hquartersDelegate.assign(mean.getId(), slot1.getId());

    }

    @Test
    public void weeksDtoWithTasksMappingTest(){
        HquarterDtoFull hquarterDtoFull = hquarterDtoFullMapper.mapToDto(hQuarter1);
        assertTrue(hquarterDtoFull.getWeeks().size()==4);
        Stack<String> tasksTitles = new Stack<>();
        tasksTitles.push("task 9");
        tasksTitles.push("task 8");
        tasksTitles.push("task 7");
        tasksTitles.push("task 6");
        tasksTitles.push("task 5");
        tasksTitles.push("task 4");
        tasksTitles.push("task 3");
        tasksTitles.push("task 2");
        tasksTitles.push("task 1");

        List<WeekWithTasksDto> weeks = hquarterDtoFull.getWeeks();

        //check first week
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.mon).size() == 1);
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.mon).get(0).getTitle().equals(tasksTitles.pop()));
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.tue) == null);
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.wed) == null);
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.thu) == null);
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.fri).size() == 1);
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.fri).get(0).getTitle().equals(tasksTitles.pop()));
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.sat) == null);
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.sun).size() == 1);
        assertTrue(weeks.get(0).getDays().get(DaysOfWeek.sun).get(0).getTitle().equals(tasksTitles.pop()));

        //check others
        for (int i = 1; i < 4; i++) {
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.mon).size() == 1);
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.mon).get(0).getTitle().equals(tasksTitles.pop()));
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.tue) == null);
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.wed) == null);
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.thu) == null);
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.fri).size() == 1);
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.fri).get(0).getTitle().equals(tasksTitles.pop()));
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.sat) == null);
            assertTrue(weeks.get(i).getDays().get(DaysOfWeek.sun) == null);
        }
    }

    private Task createTask(String title, Subject subject, int position){
        Task task = new Task(title, subject, position);
        tasksDAO.saveOrUpdate(task);
        return task;
    }



}
