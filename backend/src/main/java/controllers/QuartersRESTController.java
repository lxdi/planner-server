package controllers;

import model.IQuarterDAO;
import model.entities.Quarter;
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
    IQuarterDAO quarterDAO;

    public QuartersRESTController(){}

    public QuartersRESTController(IQuarterDAO quarterDAO){
        this.quarterDAO = quarterDAO;
    }

    @RequestMapping(path = "/all")
    public ResponseEntity<List<Quarter>> getAllQuarters(){
        List<Quarter> result = quarterDAO.getAllQuartals();
        return new ResponseEntity<List<Quarter>>(result, HttpStatus.OK);
    }

}
