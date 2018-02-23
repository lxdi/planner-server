package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Alexander on 23.02.2018.
 */

@Controller
public class MainController {

    @RequestMapping(path = "/")
    public String index(){
        return "index";
    }

}
