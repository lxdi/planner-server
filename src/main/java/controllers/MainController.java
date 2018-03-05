package controllers;

import model.ITargetsDAO;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Alexander on 23.02.2018.
 */

@Controller
public class MainController {

    @Autowired
    ITargetsDAO targetsDAO;

    @RequestMapping(path = "/")
    public String index(){
        return "index";
    }

    @RequestMapping(path = "/targets/firstlevel")
    public ResponseEntity<List<Target>> get1stLevelTargets(){
        return new ResponseEntity<List<Target>>(targetsDAO.firstLevelTargets(), HttpStatus.OK);
    }

}
