package controllers;

import controllers.delegates.RepPlanDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;
import java.util.Map;

@Controller
public class RepPlansController {

    @Autowired
    RepPlanDelegate repPlanDelegate;

    @RequestMapping(path = "/repetition/plan/all", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getAll(){
        return new ResponseEntity<>(repPlanDelegate.getAll(), HttpStatus.OK);
    }

}
