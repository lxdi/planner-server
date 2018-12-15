package controllers;

import model.dao.*;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotMapper;
import model.dto.slot.SlotPositionDtoLazy;
import model.dto.slot.SlotPositionMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

@Controller
@RequestMapping(path = "/hquarter")
public class HquartersRESTController {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO quarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    @Autowired
    SlotMapper slotMapper;

    @Autowired
    SlotPositionMapper slotPositionMapper;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    public HquartersRESTController(){}

    public HquartersRESTController(IMeansDAO iMeansDAO, IHQuarterDAO ihQuarterDAO, ISlotDAO iSlotDAO,
                                   HquarterMapper hquarterMapper, SlotMapper slotMapper, SlotPositionMapper slotPositionMapper){
        this.meansDAO = iMeansDAO;
        this.quarterDAO = ihQuarterDAO;
        this.slotDAO = iSlotDAO;
        this.hquarterMapper = hquarterMapper;
        this.slotMapper = slotMapper;
        this.slotPositionMapper = slotPositionMapper;
    }

    public HquartersRESTController(IHQuarterDAO quarterDAO){
        this.quarterDAO = quarterDAO;
    }

    @RequestMapping(path = "/all")
    public ResponseEntity<List<HquarterDtoLazy>> getAllQuarters(){
        List<HquarterDtoLazy> result = new ArrayList<>();
        for(HQuarter hQuarter : quarterDAO.getAllHQuartals()){
            result.add(hquarterMapper.mapToDto(hQuarter));
        }
        return new ResponseEntity<List<HquarterDtoLazy>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseEntity<HquarterDtoLazy> update(@RequestBody HquarterDtoLazy hquarterDto){
        HQuarter hQuarter = saveHQuarter(hquarterDto);
        return new ResponseEntity<HquarterDtoLazy>(hquarterMapper.mapToDto(hQuarter), HttpStatus.OK);
    }

    @RequestMapping(path = "/assignmean/{meanid}/toslot/{slotid}", method = RequestMethod.POST)
    public ResponseEntity<SlotDtoLazy> assign(@PathVariable("meanid") long meanid, @PathVariable("slotid") long slotid){
        Mean mean = meansDAO.meanById(meanid);
        Slot slot = slotDAO.getById(slotid);
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);
        createTaskMappers(mean, slot);
        return new ResponseEntity<>(slotMapper.mapToDto(slot), HttpStatus.OK);
    }

    @RequestMapping(path="/slot/unassign/{slotid}", method = RequestMethod.POST)
    public ResponseEntity<SlotDtoLazy> unassign(@PathVariable("slotid") long slotid){
        Slot slot = slotDAO.getById(slotid);
        slot.setMean(null);
        slotDAO.saveOrUpdate(slot);
        return new ResponseEntity<>(slotMapper.mapToDto(slot), HttpStatus.OK);
    }

    private void createTaskMappers(Mean mean, Slot slot){
        Layer layerToMap = layerDAO.getCurrentLayer(mean);
        if(layerToMap!=null){
            List<Task> tasks = tasksDAO.tasksByLayer(layerToMap);
            if(tasks.size()>0) {
                Stack<Task> taskStack = tasksInStack(tasks);
                HQuarter hQuarter = slot.getHquarter();
                Task currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                Iterator<Week> weekIterator = weekDAO.weeksOfHquarter(hQuarter).iterator();
                while(weekIterator.hasNext() && currentTask!=null){
                    Week currentWeek = weekIterator.next();
                    Iterator<SlotPosition> slotPositionIterator = slotDAO.getSlotPositionsForSlot(slot).iterator();
                    while (slotPositionIterator.hasNext() && currentTask!=null){
                        SlotPosition currentSP = slotPositionIterator.next();
                        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(currentTask);
                        if(taskMapper==null){
                            taskMapper = new TaskMapper();
                            taskMapper.setTask(currentTask);
                        }
                        taskMapper.setSlotPosition(currentSP);
                        taskMapper.setWeek(currentWeek);
                        taskMappersDAO.saveOrUpdate(taskMapper);
                        currentTask = !taskStack.isEmpty()? taskStack.pop():null;
                    }
                }
            }
        }
    }

    private Stack<Task> tasksInStack(List<Task> tasks){
        Stack<Task> result = new Stack<>();
        for(Task task : tasks){
            result.push(task);
        }
        return result;
    }

    @RequestMapping(path="/get/default", method = RequestMethod.GET)
    public ResponseEntity<HquarterDtoLazy> getDefault(){
        return new ResponseEntity<>(hquarterMapper.mapToDto(quarterDAO.getDefault()), HttpStatus.OK);
    }

    @RequestMapping(path="/set/default", method = RequestMethod.POST)
    public ResponseEntity<HquarterDtoLazy> setDefault(@RequestBody HquarterDtoLazy hquarterDtoLazy){
        assert hquarterDtoLazy.getStartWeek()==null && hquarterDtoLazy.getEndWeek()==null;
        HQuarter defaultHquarter = saveHQuarter(hquarterDtoLazy);
        List<Slot> defaultSlots = slotDAO.getSlotsForHquarter(defaultHquarter);
        if(defaultSlots.size()>0) {
            for(HQuarter hQuarter : quarterDAO.getDefaultHquarters()) {
                //List<Slot> slots = slotDAO.getSlotsForHquarter(hQuarter);
                for(Slot defaultSlot :  defaultSlots){
                    Slot slot = slotDAO.getByHquarterAndPosition(hQuarter, defaultSlot.getPosition());
                    if(slot==null){
                        slot = new Slot();
                        slot.setPosition(defaultSlot.getPosition());
                        slot.setHquarter(hQuarter);
                        slotDAO.saveOrUpdate(slot);
                    }
                    Stack<SlotPosition> slotPositionsPool = new Stack<>();
                    for(SlotPosition slotPosition : slotDAO.getSlotPositionsForSlot(slot)){
                        slotPositionsPool.push(slotPosition);
                    }
                    for(SlotPosition defaultSlotPosition : slotDAO.getSlotPositionsForSlot(defaultSlot)){
                        //SlotPosition slotPosition = slotDAO.getSlotPosition(slot, defaultSlotPosition.getDaysOfWeek(), defaultSlotPosition.getPosition());
                        SlotPosition slotPosition = !slotPositionsPool.isEmpty()? slotPositionsPool.pop():null;
                        if(slotPosition==null){
                            slotPosition = new SlotPosition();
                            slotPosition.setSlot(slot);
                            //slotDAO.saveOrUpdate(slotPosition);
                        }
                        if(slotPosition.getDaysOfWeek()!=defaultSlotPosition.getDaysOfWeek() || slotPosition.getPosition()!=defaultSlotPosition.getPosition()){
                            slotPosition.setDaysOfWeek(defaultSlotPosition.getDaysOfWeek());
                            slotPosition.setPosition(defaultSlotPosition.getPosition());
                            slotDAO.saveOrUpdate(slotPosition);
                        }
                    }
                    //TODO remove all SlotPositions left in the slotPositionsPool
                }
            }
        }
        return new ResponseEntity<>(hquarterMapper.mapToDto(defaultHquarter), HttpStatus.OK);
    }

    private HQuarter saveHQuarter(HquarterDtoLazy hquarterDtoLazy){
        HQuarter hQuarter = hquarterMapper.mapToEntity(hquarterDtoLazy);
        //TODO validate slots before saving
        quarterDAO.saveOrUpdate(hQuarter);
        saveSlots(hquarterDtoLazy.getSlots(), hQuarter.getId());
        return hQuarter;
    }

    private void saveSlots(List<SlotDtoLazy> slotsDto, long hquarterid){
        if(slotsDto!=null){
            for(SlotDtoLazy slotDto : slotsDto){
                if(slotDto!=null) {
                    slotDto.setHquarterid(hquarterid);
                    Slot slot = slotMapper.mapToEntity(slotDto);
                    //TODO validate before saving
                    slotDAO.saveOrUpdate(slot);
                    saveSlotPositions(slotDto.getSlotPositions(), slot.getId());
                }
            }
        }
    }

    private void saveSlotPositions(List<SlotPositionDtoLazy> slotsPosDto, long slotid){
        if(slotsPosDto!=null && slotsPosDto.size()>0){
            for(SlotPositionDtoLazy slotPosDto : slotsPosDto){
                if(slotPosDto!=null) {
                    slotPosDto.setSlotid(slotid);
                    //TODO validate before saving
                    slotDAO.saveOrUpdate(slotPositionMapper.mapToEntity(slotPosDto));
                }
            }
        }
    }

}
