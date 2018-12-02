package controllers;

import model.dao.IHQuarterDAO;
import model.entities.HQuarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(path = "/quarter")
public class QuartersRESTController {


    @Autowired
    IHQuarterDAO quarterDAO;

    public QuartersRESTController(){}

    public QuartersRESTController(IHQuarterDAO quarterDAO){
        this.quarterDAO = quarterDAO;
    }

    @RequestMapping(path = "/all")
    public ResponseEntity<List<HQuarter>> getAllQuarters(){
        List<HQuarter> result = quarterDAO.getAllHQuartals();
        return new ResponseEntity<List<HQuarter>>(result, HttpStatus.OK);
    }

}
