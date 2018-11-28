package controllers;

import model.dao.IWeekDAO;
import model.entities.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 23.04.2018.
 */

@Controller
public class WeeksRESTController {

    @Autowired
    IWeekDAO weekDAO;

    public WeeksRESTController(){
    }

    public WeeksRESTController(IWeekDAO weekDAO){
        this.weekDAO = weekDAO;
    }

    @RequestMapping(path = "/week/all")
    public ResponseEntity<Map<Integer, List<Week>>> getAllWeeks(){
        return new ResponseEntity<Map<Integer, List<Week>>>(weekDAO.getWeeksMap(), HttpStatus.OK);
    }


}
