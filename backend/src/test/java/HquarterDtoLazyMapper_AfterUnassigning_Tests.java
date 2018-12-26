import controllers.delegates.HquartersDelegate;
import model.dao.*;
import model.dto.hquarter.HquarterDtoFull;
import model.dto.hquarter.HquarterDtoFullMapper;
import model.dto.hquarter.WeekWithTasksDto;
import model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;

import java.util.Stack;

import static junit.framework.TestCase.assertTrue;

public class HquarterDtoLazyMapper_AfterUnassigning_Tests extends ATestsWithTargetsMeansQuartalsGenerated {


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

    HQuarter hQuarter2;
    Slot slot3;
    SlotPosition slotPosition31;
    SlotPosition slotPosition32;
    SlotPosition slotPosition33;

    @Before
    public void init(){
        super.init();

        hQuarter1 = ihQuarterDAO.getHQuartersInYear(2018).get(0);
        hQuarter2 = ihQuarterDAO.getHQuartersInYear(2018).get(7);

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
        slot2 = new Slot(hQuarter1, 2);
        slotDAO.saveOrUpdate(slot2);

        slotPosition21 = new SlotPosition(slot2, DaysOfWeek.tue, 1);
        slotDAO.saveOrUpdate(slotPosition21);

        slotPosition22 = new SlotPosition(slot2, DaysOfWeek.fri, 1);
        slotDAO.saveOrUpdate(slotPosition22);

        slotPosition23 = new SlotPosition(slot2, DaysOfWeek.fri, 2);
        slotDAO.saveOrUpdate(slotPosition23);

        //------
        slot3 = new Slot(hQuarter2, 1);
        slotDAO.saveOrUpdate(slot3);

        slotPosition31 = new SlotPosition(slot3, DaysOfWeek.mon, 3);
        slotDAO.saveOrUpdate(slotPosition31);

        slotPosition32 = new SlotPosition(slot3, DaysOfWeek.thu, 1);
        slotDAO.saveOrUpdate(slotPosition32);

        slotPosition33 = new SlotPosition(slot3, DaysOfWeek.fri, 1);
        slotDAO.saveOrUpdate(slotPosition33);

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

        Layer layer2 = new Layer(mean, 2);
        layerDAO.saveOrUpdate(layer2);

        //--
        Subject subject21 = new Subject(layer2, 1);
        subject1.setLayer(layer2);
        subjectDAO.saveOrUpdate(subject21);

        createTask("task 2-1", subject21, 1);
        createTask("task 2-4", subject21, 4);
        createTask("task 2-5", subject21, 5);
        createTask("task 2-2", subject21, 2);
        createTask("task 2-3", subject21, 3);
        createTask("task 2-6", subject21, 6);
        createTask("task 2-7", subject21, 7);
        createTask("task 2-9", subject21, 9);
        createTask("task 2-8", subject21, 8);

        Layer layer3 = new Layer(mean, 3);
        layerDAO.saveOrUpdate(layer3);

    }

    @Test
    public void unassignMiddleSlotTest(){
        hquartersDelegate.assign(mean.getId(), slot1.getId());
        hquartersDelegate.assign(mean.getId(), slot2.getId());
        hquartersDelegate.assign(mean.getId(), slot3.getId());

        hquartersDelegate.unassign(slot2.getId());

        assertTrue(slotDAO.getById(slot1.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getById(slot1.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 1).getId());

        assertTrue(slotDAO.getById(slot2.getId()).getMean()==null);
        assertTrue(slotDAO.getById(slot2.getId()).getLayer()==null);

        assertTrue(slotDAO.getById(slot3.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getById(slot3.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 2).getId());


        HquarterDtoFull hquarterDtoFull = hquarterDtoFullMapper.mapToDto(hQuarter1);
        assertTrue(hquarterDtoFull.getWeeks().size()==6);
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
        boolean odd = true;
        for(WeekWithTasksDto weekWithTasksDto : hquarterDtoFull.getWeeks()) {
            if(odd) {
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.mon).size() == 1);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.mon).get(0).getTitle().equals(tasksTitles.pop()));
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.tue) == null);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.wed) == null);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.thu) == null);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.fri).size() == 1);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.fri).get(0).getTitle().equals(tasksTitles.pop()));
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.sat) == null);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.sun).size() == 1);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.sun).get(0).getTitle().equals(tasksTitles.pop()));
            } else {
                for(DaysOfWeek dayOfWeek : DaysOfWeek.values()){
                    assertTrue(weekWithTasksDto.getDays().get(dayOfWeek) == null);
                }
            }
            odd = !odd;
        }

        HquarterDtoFull hquarterDtoFull2 = hquarterDtoFullMapper.mapToDto(hQuarter2);
        assertTrue(hquarterDtoFull2.getWeeks().size()==6);
        Stack<String> tasksTitles2 = new Stack<>();
        tasksTitles2.push("task 2-9");
        tasksTitles2.push("task 2-8");
        tasksTitles2.push("task 2-7");
        tasksTitles2.push("task 2-6");
        tasksTitles2.push("task 2-5");
        tasksTitles2.push("task 2-4");
        tasksTitles2.push("task 2-3");
        tasksTitles2.push("task 2-2");
        tasksTitles2.push("task 2-1");
        odd = true;
        for(WeekWithTasksDto weekWithTasksDto : hquarterDtoFull2.getWeeks()) {
            if(odd) {
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.mon).size() == 1);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.mon).get(0).getTitle().equals(tasksTitles2.pop()));
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.tue) == null);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.wed) == null);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.thu).size() == 1);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.thu).get(0).getTitle().equals(tasksTitles2.pop()));
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.fri).size() == 1);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.fri).get(0).getTitle().equals(tasksTitles2.pop()));
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.sat) == null);
                assertTrue(weekWithTasksDto.getDays().get(DaysOfWeek.sun) == null);
            } else {
                for(DaysOfWeek dayOfWeek : DaysOfWeek.values()){
                    assertTrue(weekWithTasksDto.getDays().get(dayOfWeek) == null);
                }
            }
            odd = !odd;
        }

    }

    @Test
    public void unassignAll(){
        hquartersDelegate.assign(mean.getId(), slot1.getId());
        hquartersDelegate.assign(mean.getId(), slot2.getId());
        hquartersDelegate.assign(mean.getId(), slot3.getId());

        assertTrue(slotDAO.getById(slot1.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getById(slot1.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 1).getId());

        assertTrue(slotDAO.getById(slot2.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getById(slot2.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 2).getId());

        assertTrue(slotDAO.getById(slot3.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getById(slot3.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 3).getId());

        hquartersDelegate.unassign(slot1.getId());
        hquartersDelegate.unassign(slot2.getId());
        hquartersDelegate.unassign(slot3.getId());

        assertTrue(slotDAO.getById(slot1.getId()).getMean()==null);
        assertTrue(slotDAO.getById(slot1.getId()).getLayer()==null);

        assertTrue(slotDAO.getById(slot2.getId()).getMean()==null);
        assertTrue(slotDAO.getById(slot2.getId()).getLayer()==null);

        assertTrue(slotDAO.getById(slot3.getId()).getMean()==null);
        assertTrue(slotDAO.getById(slot3.getId()).getLayer()==null);

        HquarterDtoFull hquarterDtoFull = hquarterDtoFullMapper.mapToDto(hQuarter1);
        assertTrue(hquarterDtoFull.getWeeks().size()==6);
        for(WeekWithTasksDto weekWithTasksDto : hquarterDtoFull.getWeeks()) {
            for(DaysOfWeek dayOfWeek : DaysOfWeek.values()){
                assertTrue(weekWithTasksDto.getDays().get(dayOfWeek) == null);
            }
        }

        HquarterDtoFull hquarterDtoFull2 = hquarterDtoFullMapper.mapToDto(hQuarter2);
        assertTrue(hquarterDtoFull2.getWeeks().size()==6);
        for(WeekWithTasksDto weekWithTasksDto : hquarterDtoFull2.getWeeks()) {
            for(DaysOfWeek dayOfWeek : DaysOfWeek.values()){
                assertTrue(weekWithTasksDto.getDays().get(dayOfWeek) == null);
            }
        }
    }

    private void checkTaskMapper(TaskMapper taskMapper, Week week, SlotPosition slotPosition){
        assertTrue(taskMapper.getSlotPosition().getId()==slotPosition.getId());
        assertTrue(taskMapper.getWeek().getId()==week.getId());
    }

    private Task createTask(String title, Subject subject, int position){
        Task task = new Task(title, subject, position);
        tasksDAO.saveOrUpdate(task);
        return task;
    }

}
