package controllers;

import model.dao.IHQuarterDAO;
import model.dao.ISlotDAO;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotMapper;
import model.dto.slot.SlotPositionDtoLazy;
import model.dto.slot.SlotPositionMapper;
import model.entities.HQuarter;
import model.entities.Slot;
import model.entities.SlotPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping(path = "/quarter")
public class HquartersRESTController {


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

    public HquartersRESTController(){}

    public HquartersRESTController(IHQuarterDAO quarterDAO){
        this.quarterDAO = quarterDAO;
    }

    @RequestMapping(path = "/all")
    public ResponseEntity<List<HQuarter>> getAllQuarters(){
        List<HQuarter> result = quarterDAO.getAllHQuartals();
        return new ResponseEntity<List<HQuarter>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseEntity<HquarterDtoLazy> update(@RequestBody HquarterDtoLazy hquarterDto){
        HQuarter hQuarter = hquarterMapper.mapToEntity(hquarterDto);
        //TODO validate slots before saving
        quarterDAO.saveOrUpdate(hQuarter);
        saveSlots(hquarterDto.getSlots(), hQuarter.getId());
        return new ResponseEntity<HquarterDtoLazy>(hquarterMapper.mapToDto(hQuarter), HttpStatus.OK);
    }

    private void saveSlots(List<SlotDtoLazy> slotsDto, long hquarterid){
        if(slotsDto!=null){
            for(SlotDtoLazy slotDto : slotsDto){
                slotDto.setHquarterid(hquarterid);
                Slot slot = slotMapper.mapToEntity(slotDto);
                slotDAO.saveOrUpdate(slot);
                saveSlotPositions(slotDto.getSlotPositions(), slot.getId());
            }
        }
    }

    private void saveSlotPositions(List<SlotPositionDtoLazy> slotsPosDto, long slotid){
        if(slotsPosDto!=null && slotsPosDto.size()>0){
            for(SlotPositionDtoLazy slotPosDto : slotsPosDto){
                slotPosDto.setSlotid(slotid);
                slotDAO.saveOrUpdate(slotPositionMapper.mapToEntity(slotPosDto));
            }
        }
    }

}
